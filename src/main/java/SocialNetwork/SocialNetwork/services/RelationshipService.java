package SocialNetwork.SocialNetwork.services;


import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;

import java.util.List;

public interface RelationshipService{
    boolean addFollow(User user, String username);
//    List<User> getAllFriendOfUser(String phone);
    List<User> getFollower(String username);
    List<User> getFollowing(String username);
    Long countFollower(Long userId);
    Long countFollowing(Long userId);
    boolean checkFollow(User user,String username);
    List<UserDTO> recommendUser(String username);
}
