package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.LikeRepository;
import SocialNetwork.SocialNetwork.repositories.PostRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.LikeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {
    private LikeRepository likeRepository;
    private PostRepository postRepository;
    private UserRepository userRepository;

    public LikeServiceImpl(LikeRepository likeRepository, PostRepository postRepository, UserRepository userRepository) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean addLike(Integer postId, User user) throws CustomException {
        Post post = postRepository.findById(postId).orElse(null);
        if ( post == null) {
            throw new CustomException("PostId not found");
        }
        Like checkLike = likeRepository.findByUserAndPost(user, post);
        if ( checkLike != null ) {
            post.setLikeCount(post.getLikeCount()-1); ;
            postRepository.save(post);
            likeRepository.delete(checkLike);
        }else {
            Like like = new Like();
            like.setUserId(user.getId());
            like.setPostId(post.getId());
            post.setLikeCount(post.getLikeCount());
            postRepository.save(post);
            likeRepository.save(like);
        }
        return true;
    }

    @Override
    public int getAllLikesForPost(Integer postId) throws CustomException{
        Post post = this.postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not exists");
        }
        return this.likeRepository.findAllLikesByPost(post).size();
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

    @Override
    public Integer getAllLikes() {
        return this.likeRepository.findAll().size();
    }
}
