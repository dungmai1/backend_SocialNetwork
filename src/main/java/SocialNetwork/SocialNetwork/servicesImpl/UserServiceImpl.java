package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.config.JwtService;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.PostService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Override
    public User findUserByJwt(String jwt) {
        String token = jwt.substring(7);
        String phone = jwtService.extractUsername(token);
        User user = userRepository.findByPhone(phone).orElse(null);
        if(user==null) {
            throw new CustomException("User not exist with phone "+phone);
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
