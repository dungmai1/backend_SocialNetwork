package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;

import java.util.List;

public interface UserService {
    public UserDTO findUser(String jwt);
    public User findUserByJwt(String jwt);

    User findUserByUsername(String username);
    List<User> searchUserName(String textSearch);

    List<User> getAllUser();

    void updateUser(User user, String avatar);

    void banUser(Integer UserId);
    void UnbanUser(Integer UserId);

}
