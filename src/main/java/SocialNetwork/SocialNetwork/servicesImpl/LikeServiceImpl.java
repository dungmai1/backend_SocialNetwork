package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.TargetType;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.CommentRepository;
import SocialNetwork.SocialNetwork.repositories.LikeRepository;
import SocialNetwork.SocialNetwork.repositories.PostRepository;
import SocialNetwork.SocialNetwork.services.LikeService;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@EnableCaching
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository, CommentRepository commentRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "post:likeCount", key = "#postId"),
        @CacheEvict(value = "post:likeUsers", key = "#postId")
    })
    public boolean addLikePost(Long postId, User user) throws CustomException {
        Post post = postRepository.findById(postId).orElse(null);
        if ( post == null) {
            throw new CustomException("PostId not found");
        }
        Like checkLike = likeRepository.findByUserAndTargetType(postId, TargetType.POST, user.getId());
        if ( checkLike != null ) {
            post.setLikeCount(post.getLikeCount() - 1);
            postRepository.save(post);
            likeRepository.delete(checkLike);
        }else {
            Like like = new Like();
            like.setUser(user);
            like.setTargetId(postId);
            like.setTargetType(TargetType.POST);
            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);
            likeRepository.save(like);
        }
        return true;
    }
    @Cacheable(value = "post:likeCount", key = "#postId")
    @Override
    public Long getPostLikeCount(Long postId) throws CustomException{
        Post post = this.postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not exists");
        }
        return this.likeRepository.countLikes(post.getId(), TargetType.POST);
    }

    @Override
    @Cacheable(value = "post:likeUsers", key = "#postId")
    public List<UserDTO> getAllUserLikePost(Long postId) throws CustomException{
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("PostId not exists");
        }
        List<Like> likes = likeRepository.getAllUserLike(postId, TargetType.POST);
        if (likes != null && !likes.isEmpty()) {
            return likes.stream().map(like -> 
            new UserDTO(
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getUser().getDisplayname(),
                like.getUser().getAvatar()
            )).collect(Collectors.toList());
        }
        return List.of();
    }
    @Override
    @Caching(evict = {
        @CacheEvict(value = "comment:likeCount", key = "#commentId"),
        @CacheEvict(value = "comment:likeUsers", key = "#commentId")
    })
    public boolean addLikeComment(Long commentId, User user) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if ( comment == null) {
            throw new CustomException("CommentId not found");
        }
        Like checkLike = likeRepository.findByUserAndTargetType(commentId, TargetType.COMMENT, user.getId());
        if (checkLike != null) {
            likeRepository.delete(checkLike);
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setTargetId(commentId);
            like.setTargetType(TargetType.COMMENT);
            likeRepository.save(like);
        }
        return true;
    }
    @Cacheable(value = "comment:likeCount", key = "#commentId")
    @Override
    public Long getCommentLikeCount(Long commentId) throws CustomException{
        Comment comment = this.commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new CustomException("Comment not exists");
        }
        return this.likeRepository.countLikes(comment.getId(), TargetType.COMMENT);
    }
        @Override
    @Cacheable(value = "comment:likeUsers", key = "#commentId")
    public List<UserDTO> getAllUserLikeComment(Long commentId) throws CustomException{
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) {
            throw new CustomException("CommentId not exists");
        }
        List<Like> likes = likeRepository.getAllUserLike(commentId, TargetType.COMMENT);
        if (likes != null && !likes.isEmpty()) {
            return likes.stream().map(like -> 
            new UserDTO(
                like.getUser().getId(),
                like.getUser().getUsername(),
                like.getUser().getDisplayname(),
                like.getUser().getAvatar()
            )).collect(Collectors.toList());
        }
        return List.of();
    }
    @Override
    public  boolean hasUserLiked(Long postId, Long userId){
        return likeRepository.existsByTargetIdAndTargetTypeAndUser(postId, TargetType.POST, userId);
    }

}
