package SocialNetwork.SocialNetwork.services;


import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserServiceModel;

import java.util.List;

public interface RelationshipService{
    boolean CreateRequestAddingFriend(User user, String username);
//    List<User> getAllFriendOfUser(String phone);
    List<User> getFollower(String username);
    List<User> getFollowing(String username);
    boolean checkFollow(User user,String username);
}
