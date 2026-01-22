package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.TargetType;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.CommentRequest;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.EditCommentRequest;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.RepCommentRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CommentDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.CommentRepository;
import SocialNetwork.SocialNetwork.repositories.LikeRepository;
import SocialNetwork.SocialNetwork.repositories.PostRepository;
import SocialNetwork.SocialNetwork.services.CommentService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private ModelMapper modelMapper;
    private LikeRepository likeRepository;
    @Autowired
    private CacheManager cacheManager;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository,
            ModelMapper modelMapper, LikeRepository likeRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
        this.likeRepository = likeRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "comments:postId", key = "#commentRequest.postId"),
            @CacheEvict(value = "commentLists:postId", key = "#commentRequest.postId")
    })
    public CommentDTO addComment(CommentRequest commentRequest, User user) {
        Post post = postRepository.findById(commentRequest.getPostId()).orElse(null);
        if (post == null) {
            throw new CustomException("PostId not found");
        }
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContentCmt());
        comment.setPost(post);
        comment.setUser(user);
        comment.setImageUrl(commentRequest.getImageUrl());
        comment.setCommentTime(LocalDateTime.now());
        commentRepository.save(comment);
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar(),
                comment.getImageUrl(),
                comment.getCommentTime(),
                comment.getParentId());
    }

    @Override
    @Cacheable(value = "comments:postId", key = "#postId")
    public int CountAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("PostId not found");
        }
        return this.commentRepository.countByPost(post);
    }

    @Transactional
    @Override
    @Caching(evict = {
            @CacheEvict(value = "comments:postId", key = "#postId"),
            @CacheEvict(value = "commentLists:postId", key = "#postId")
    })
    public void deleteComment(User user, Long postId, Long commentId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException("Post not found"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException("Comment not found"));
        boolean isCommentOwner = comment.getUser().getId().equals(user.getId());
        boolean isPostOwner = post.getUser().getId().equals(user.getId());
        if (!isCommentOwner && !isPostOwner) {
            throw new CustomException("You do not have permission to delete this comment");
        }
        likeRepository.deleteByTargetTypeAndTargetId(TargetType.COMMENT, comment.getId());
        safeEvict("replies:commentId", comment.getParentId());
        safeEvict("repliesLists:commentId", comment.getParentId());
        if (comment.getParentId() == null) {
            commentRepository.deleteAllCommentByParentId(post, comment.getId());
            commentRepository.delete(comment);
        } else {
            commentRepository.delete(comment);
        }
    }

    @Override
    @Cacheable(value = "commentLists:postId", key = "#postId")
    public List<CommentDTO> getAllCommentForPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("userId or PostId not found");
        }
        List<Comment> commentList = commentRepository.findAllByPost(post);
        return commentList.stream()
                .map(c -> new CommentDTO(
                        c.getId(),
                        c.getContent(),
                        c.getPost().getId(),
                        c.getUser().getUsername(),
                        c.getUser().getAvatar(),
                        c.getImageUrl(),
                        c.getCommentTime(),
                        c.getParentId()))
                .collect(Collectors.toList());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "replies:commentId", key = "#repCommentRequest.commentId"),
            @CacheEvict(value = "repliesLists:commentId", key = "#repCommentRequest.commentId")
    })
    public CommentDTO addRepComment(RepCommentRequest repCommentRequest, User user) {
        Comment parentComment = commentRepository.findById(repCommentRequest.getCommentId()).orElse(null);
        if (parentComment == null) {
            throw new CustomException("CommentId not found");
        }
        Comment replyComment = new Comment();
        replyComment.setContent(repCommentRequest.getContent());
        replyComment.setPost(parentComment.getPost());
        replyComment.setUser(user);
        replyComment.setParentId(parentComment.getId());
        replyComment.setCommentTime(LocalDateTime.now());
        commentRepository.save(replyComment);
        safeEvict("comments:postId", parentComment.getPost().getId());
        return new CommentDTO(
                replyComment.getId(),
                replyComment.getContent(),
                replyComment.getPost().getId(),
                replyComment.getUser().getUsername(),
                replyComment.getUser().getAvatar(),
                replyComment.getImageUrl(),
                replyComment.getCommentTime(),
                replyComment.getParentId());
    }

    @Override
    @Cacheable(value = "replies:commentId", key = "#commentId")
    public int CountAllRepComment(Long commentId) {
        Comment parentComment = commentRepository.findById(commentId).orElse(null);
        if (parentComment == null) {
            throw new CustomException("CommentId not found");
        }
        return this.commentRepository.countRepComment(commentId);
    }

    @Override
    @Cacheable(value = "repliesLists:commentId", key = "#commentId")
    public List<CommentDTO> getAllRepComment(Long commentId) {
        Comment parentComment = commentRepository.findById(commentId).orElse(null);
        if (parentComment == null) {
            throw new CustomException("CommentId not found");
        }
        List<Comment> commentList = commentRepository.findAllByRepComment(parentComment.getPost(), commentId);
        return commentList.stream()
                .map(c -> new CommentDTO(
                        c.getId(),
                        c.getContent(),
                        c.getPost().getId(),
                        c.getUser().getUsername(),
                        c.getUser().getAvatar(),
                        c.getImageUrl(),
                        c.getCommentTime(),
                        c.getParentId()))
                .collect(Collectors.toList());
    }

    private void safeEvict(String cacheName, Object key) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "comments:postId", key = "#editCommentRequest.postId"),
            @CacheEvict(value = "commentLists:postId", key = "#editCommentRequest.postId")
    })
    public CommentDTO editComment(EditCommentRequest editCommentRequest, User user) {
        Post post = postRepository.findById(editCommentRequest.getPostId())
                .orElseThrow(() -> new CustomException("Post not found"));
        Comment comment = commentRepository.findById(editCommentRequest.getCommentId())
                .orElseThrow(() -> new CustomException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException("You do not have permission to edit this comment");
        }

        if (!comment.getPost().getId().equals(post.getId())) {
            throw new CustomException("Comment does not belong to this post");
        }

        comment.setContent(editCommentRequest.getContent());
        commentRepository.save(comment);

        if (comment.getParentId() != null) {
            safeEvict("replies:commentId", comment.getParentId());
            safeEvict("repliesLists:commentId", comment.getParentId());
        }
        return new CommentDTO(
                comment.getId(),
                comment.getContent(),
                comment.getPost().getId(),
                comment.getUser().getUsername(),
                comment.getUser().getAvatar(),
                comment.getImageUrl(),
                comment.getCommentTime(),
                comment.getParentId());
    }

}
