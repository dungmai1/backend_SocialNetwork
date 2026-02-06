package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserProfileDTO;

import java.util.List;

import SocialNetwork.SocialNetwork.domain.models.requests.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    public UserDTO findUser(String jwt);

    public User findUserByJwt(String jwt);

    UserProfileDTO findUserByUsername(User currentUser, String username);

    List<User> searchUserName(String textSearch);

    List<User> getAllUser();

    void updateUser(User user, UserUpdateRequest request, MultipartFile avatar);

    void banUser(Integer UserId);

    void UnbanUser(Integer UserId);

}
