package SocialNetwork.SocialNetwork.services;


import SocialNetwork.SocialNetwork.domain.entities.User;

import java.util.List;

public interface LikeService {
    boolean addLike(Integer postId, User user);
    Long getLikeCount(Long postId);
    List<User> getAllUserLikePost(Integer postId);
}
