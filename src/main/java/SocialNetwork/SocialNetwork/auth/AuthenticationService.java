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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JavaMailSender mailSender;

    @Value("${app.reset-password.url}")
    private String resetPasswordUrl;

    public AuthenticationResponse register(RegisterRequest request) {
        User checkEmail = userRepository.findByEmail(request.getEmail());
        User checkUsername = userRepository.findByUsername(request.getUsername()).orElse(null);
        if (checkEmail != null) {
            throw new CustomException("User with this email already exists");
        } else if (checkUsername != null) {
            throw new CustomException("Username already exists");
        } else {
            var user = User.builder()
                    .username(request.getUsername())
                    .displayname(request.getDisplayname())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ROLE_USER)
                    .avatar("https://ui-avatars.com/api/?name="
                            + (request.getDisplayname() != null ? request.getDisplayname() : request.getUsername())
                                    .replace(" ", "+")
                            + "&background=random&color=fff&format=svg")
                    .status(1)
                    .build();
            userRepository.save(user);
            return AuthenticationResponse.builder()
                    .message("Registration successful")
                    .build();
        }
    }

    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {
        User checkUsername = userRepository.findByUsername(request.getUsername()).orElse(null);

        if (checkUsername == null) {
            throw new CustomException("Username not found");
        } else {
            if (passwordEncoder.matches(request.getPassword(), checkUsername.getPassword())) {
                String accessToken = jwtService.generateToken(checkUsername.getUsername(), 15);
                String refreshToken = jwtService.generateToken(checkUsername.getUsername(), 10080);
                ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/")
                        .sameSite("Lax")
                        .maxAge(Duration.ofDays(7))
                        .build();
                ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .secure(false)
                        .path("/api/v1/auth/refresh")
                        .sameSite("Lax")
                        .maxAge(Duration.ofDays(7))
                        .build();
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .message("Login successful")
                        .build();
            } else {
                throw new CustomException("Incorrect password");
            }
        }
    }

    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractCookie(request, "refreshToken");
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing refresh token");
        }
        String username = jwtService.validateToken(refreshToken);
        if (username == null) {
            throw new CustomException("Invalid JWT token");
        }
        String accessToken = jwtService.generateToken(username, 15);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .message("Get refreshToken successful")
                .build();
    }

    public String extractCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null)
            return null;

        return Arrays.stream(cookies)
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse(null);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", null)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", null)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth/refresh")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    // ==================== CHANGE PASSWORD ====================
    public void changePassword(String jwt, ChangePasswordRequest request) {
        String username = jwtService.validateToken(jwt);
        if (username == null) {
            throw new CustomException("Invalid JWT token");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new CustomException("Current password is incorrect");
        }
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            throw new CustomException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // ==================== FORGOT PASSWORD ====================
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new CustomException("No account found with this email");
        }

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
        userRepository.save(user);

        String resetLink = resetPasswordUrl + "?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Reset your password - Social Network");
        message.setText("Hello " + user.getDisplayname() + ",\n\n"
                + "You have requested to reset your password.\n"
                + "Click the link below to reset your password:\n\n"
                + resetLink + "\n\n"
                + "This link will expire in 15 minutes.\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "Social Network Team");
        mailSender.send(message);
    }

    // ==================== RESET PASSWORD ====================
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new CustomException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            throw new CustomException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
