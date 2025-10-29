package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.config.JwtService;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserProfileDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.RelationshipRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.UserService;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RelationshipRepository relationshipRepository;
    
    @Autowired
    private JwtService jwtService;
    private final ModelMapper modelMapper;
    public UserServiceImpl(UserRepository userRepository,JwtService jwtService ,ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }
    @Override
    public User findUserByJwt(String jwt) {
        // Validate token and get username
        String username = jwtService.validateToken(jwt);
        if (username == null) {
            throw new CustomException("Invalid JWT token");
        }
        // Find user by username
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException("User not found for token");
        }
        return user;
    }

    @Override
    public UserDTO findUser(String token) {
        // Validate token and get username
        String username = jwtService.validateToken(token);
        if (username == null) {
            throw new CustomException("Invalid JWT token");
        }
        
        // Find user by username
        User user = userRepository.findByUsername(username).orElse(null);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        if (user == null) {
            throw new CustomException("User not found for token");
        }
        
        return userDTO;
    }

    @Override
    public UserProfileDTO findUserByUsername(User currentUser, String username) {
        User targetUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));
        // UserProfileDTO userProfileDTO = new UserProfileDTO(user.getId(),user.getUsername())
        boolean isSelf = currentUser.getUsername().equals(username);
        long followerCount = relationshipRepository.countFollower(targetUser.getId());
        long followingCount = relationshipRepository.countFollowing(targetUser.getId());
        boolean isFollower = relationshipRepository.existsByUserOneAndUserTwo(currentUser.getId(),targetUser.getId());
        boolean isFollowing = relationshipRepository.existsByUserTwoAndUserOne(targetUser.getId(),currentUser.getId());
        
        UserProfileDTO.RelationshipInfo relationshipInfo = new UserProfileDTO.RelationshipInfo(isSelf,isFollowing,isFollower,followerCount,followingCount);
        return new UserProfileDTO(
            targetUser.getId(),
            targetUser.getUsername(),
            targetUser.getAvatar(),
            targetUser.getDescription(),
            relationshipInfo
        );
    }

    @Override
    public List<User> searchUserName(String textSearch) {
        List<User> user = userRepository.findByUsernameContaining(textSearch);
        if(user==null) {
            throw new CustomException("User not exist with username "+textSearch);
        }
        return user;
    }

    @Override
    public List<User> getAllUser() {
        List<User> user = userRepository.findAll();
        return user;
    }

    @Override
    public void updateUser(User user, String avatar) {
        if (user != null) {
            user.setAvatar(avatar);
            userRepository.save(user);
        }
        else{
            throw new CustomException("User not exist with username "+user.getUsername());
        }
    }

    @Override
    public void banUser(Integer UserId) {
        // User user = userRepository.findById(UserId).orElse(null);
        // if(user==null) {
        //     throw new CustomException("User not exist with username "+user.getUsername());
        // }
        // user.setStatus(2);
        // userRepository.save(user);
    }

    @Override
    public void UnbanUser(Integer UserId) {
        // User user = userRepository.findById(UserId).orElse(null);
        // if(user==null) {
        //     throw new CustomException("User not exist with username "+user.getUsername());
        // }
        // user.setStatus(1);
        // userRepository.save(user);
    }
}
