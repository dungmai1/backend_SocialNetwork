package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.User;

import java.util.List;

public interface UserService {
    public User findUserByJwt(String jwt);

    User findUserByUsername(String username);
    List<User> searchUserName(String textSearch);

    List<User> getAllUser();

    void updateUser(User user, String avatar);

    void banUser(Integer UserId);
    void UnbanUser(Integer UserId);

}
