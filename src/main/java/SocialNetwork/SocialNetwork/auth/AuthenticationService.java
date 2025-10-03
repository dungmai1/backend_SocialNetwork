package SocialNetwork.SocialNetwork.auth;
import SocialNetwork.SocialNetwork.config.JwtService;
import SocialNetwork.SocialNetwork.domain.entities.Role;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Arrays;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthenticationResponse register(RegisterRequest request) {
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
    public AuthenticationResponse login (LoginRequest request, HttpServletResponse response){
        User checkUsername = userRepository.findByUsername(request.getUsername()).orElse(null);
        
        if (checkUsername == null) {
            throw new CustomException("Username not found");
        }else{
            if(passwordEncoder.matches(request.getPassword(),checkUsername.getPassword())){
                String accessToken = jwtService.generateToken(checkUsername.getUsername(),15);
                String refreshToken = jwtService.generateToken(checkUsername.getUsername(),10080);
                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(false)              // HTTPS
                    .path("/api/auth/refresh") // cookie chỉ gửi khi gọi /refresh
                    .sameSite("None")
                    .maxAge(Duration.ofDays(7))
                    .build();
                response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .message("Login successful")
                        .build();
            }else{
                throw new CustomException("Incorrect password");
            }
        }
    }
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response){
        String refreshToken = Arrays.stream(request.getCookies())
            .filter(c -> c.getName().equals("refreshToken"))
            .findFirst()
            .map(Cookie::getValue)
            .orElseThrow(() -> new RuntimeException("No refresh token"));
        String username = jwtService.validateToken(refreshToken);
        if (username == null) {
            throw new CustomException("Invalid JWT token");
        }
        return AuthenticationResponse.builder()
                .accessToken(jwtService.generateToken(username,15))
                .message("Get refreshToken successful")
                .build();
    }
}
