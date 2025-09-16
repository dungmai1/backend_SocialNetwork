package SocialNetwork.SocialNetwork.services;


import SocialNetwork.SocialNetwork.domain.entities.User;

import java.util.List;

public interface LikeService {
    boolean addLike(Integer postId, User user);

    int getAllLikesForPost(Integer postId);
//    boolean unlike(Integer postId, User user);
    List<User> getAllUserLikePost(Integer postId);

    Integer getAllLikes();
}
