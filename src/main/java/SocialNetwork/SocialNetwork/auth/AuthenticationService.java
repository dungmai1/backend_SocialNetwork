package SocialNetwork.SocialNetwork.auth;
import SocialNetwork.SocialNetwork.domain.entities.Role;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public AuthenticationResponse register(AuthenticationRequest request) {
        User checkPhone = userRepository.findByPhone(request.getPhone()).orElse(null);
        User checkUsername = userRepository.findByUsername(request.getUsername()).orElse(null);
        if(checkPhone!=null){
            throw new CustomException("User with this phone number already exists");
        }
        else if (checkUsername !=null) {
            throw new CustomException("Username already exists");
        }
        else{
            var user = User.builder()
                    .username(request.getUsername())
                    .displayname(request.getDisplayname())
                    .phone(request.getPhone())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ROLE_USER)
                    .avatar("https://static.vecteezy.com/system/resources/previews/005/005/788/original/user-icon-in-trendy-flat-style-isolated-on-grey-background-user-symbol-for-your-web-site-design-logo-app-ui-illustration-eps10-free-vector.jpg")
                    .status(1)
                    .build();
            userRepository.save(user);
            return AuthenticationResponse.builder()
                    .message("Registration successful")
                    .build();
        }
    }
}
