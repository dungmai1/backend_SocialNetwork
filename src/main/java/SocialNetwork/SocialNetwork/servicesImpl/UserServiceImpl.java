package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.config.JwtService;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtService jwtService;
    
    @Override
    public User findUserByJwt(String jwt) {
        // Remove "Bearer " prefix if present
        String token = jwt;
        if (jwt.startsWith("Bearer ")) {
            token = jwt.substring(7);
        }
        
        // Validate token and get username
        String username = jwtService.validateToken(token);
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
    public User findUserByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if(user==null) {
            throw new CustomException("User not exist with username "+username);
        }
        return user;
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
        User user = userRepository.findById(UserId).orElse(null);
        if(user==null) {
            throw new CustomException("User not exist with username "+user.getUsername());
        }
        user.setStatus(2);
        userRepository.save(user);
    }

    @Override
    public void UnbanUser(Integer UserId) {
        User user = userRepository.findById(UserId).orElse(null);
        if(user==null) {
            throw new CustomException("User not exist with username "+user.getUsername());
        }
        user.setStatus(1);
        userRepository.save(user);
    }
}
