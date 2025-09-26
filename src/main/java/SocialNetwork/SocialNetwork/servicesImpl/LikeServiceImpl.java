package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.LikeRepository;
import SocialNetwork.SocialNetwork.repositories.PostRepository;
import SocialNetwork.SocialNetwork.services.LikeService;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableCaching
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
    }

    @Override
    public boolean addLike(Integer postId, User user) throws CustomException {
        // Post post = postRepository.findById(postId).orElse(null);
        // if ( post == null) {
        //     throw new CustomException("PostId not found");
        // }
        // Like checkLike = likeRepository.findByUserAndPost(user, post);
        // if ( checkLike != null ) {
        //     post.setLikeCount(post.getLikeCount() - 1); ;
        //     postRepository.save(post);
        //     likeRepository.delete(checkLike);
        // }else {
        //     Like like = new Like();
        //     like.setUser(user);
        //     like.setPost(post);
        //     post.setLikeCount(post.getLikeCount() + 1);
        //     postRepository.save(post);
        //     likeRepository.save(like);
        // }
        return true;
    }
    @Cacheable("likes")
    @Override
    public Long getLikeCount(Long postId) throws CustomException{
        Post post = this.postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not exists");
        }
        return this.likeRepository.countByPost(post);
    }

//    @Override
//    public boolean unlike(Integer postId,  User user) {
//        Post post = postRepository.findById(postId).orElse(null);
//        Like likeByUserAndPost = likeRepository.findByUserAndPost(user, post);
//        if (likeByUserAndPost != null) {
//            likeRepository.delete(likeByUserAndPost);
//        }
//        return true;
//    }

    @Override
    public List<User> getAllUserLikePost(Integer postId) throws CustomException{
        // Post post = postRepository.findById(postId).orElse(null);
        // if (post == null) {
        //     throw new CustomException("PostId not exists");
        // }
        // List<Like> likes = likeRepository.findAllLikesByPost(post);
        // List<User> users = new ArrayList<>();
        // for (Like like : likes) {
        //     users.add(like.getUser());
        // }
        return null;
    }
}
