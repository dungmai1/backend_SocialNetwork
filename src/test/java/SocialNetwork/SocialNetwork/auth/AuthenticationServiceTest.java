package SocialNetwork.SocialNetwork.auth;

import SocialNetwork.SocialNetwork.config.JwtService;
import SocialNetwork.SocialNetwork.domain.entities.Role;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthenticationService Tests")
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "resetPasswordUrl", "http://localhost:3000/reset-password");
    }

    @Nested
    @DisplayName("register()")
    class RegisterTests {

        @Test
        @DisplayName("Đăng ký thành công với thông tin hợp lệ")
        void register_ValidRequest_ReturnsSuccessMessage() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .displayname("New User")
                    .email("newuser@example.com")
                    .password("password123")
                    .build();

            when(userRepository.findByEmail(request.getEmail())).thenReturn(null);
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());
            when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            AuthenticationResponse response = authenticationService.register(request);

            assertNotNull(response);
            assertEquals("Registration successful", response.getMessage());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Đăng ký thất bại khi email đã tồn tại")
        void register_DuplicateEmail_ThrowsCustomException() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("newuser")
                    .email("existing@example.com")
                    .password("password123")
                    .build();

            User existingUser = User.builder().email("existing@example.com").build();
            when(userRepository.findByEmail(request.getEmail())).thenReturn(existingUser);

            CustomException exception = assertThrows(CustomException.class,
                    () -> authenticationService.register(request));

            assertEquals("User with this email already exists", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Đăng ký thất bại khi username đã tồn tại")
        void register_DuplicateUsername_ThrowsCustomException() {
            RegisterRequest request = RegisterRequest.builder()
                    .username("existinguser")
                    .email("new@example.com")
                    .password("password123")
                    .build();

            User existingUser = User.builder().username("existinguser").build();
            when(userRepository.findByEmail(request.getEmail())).thenReturn(null);
            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(existingUser));

            CustomException exception = assertThrows(CustomException.class,
                    () -> authenticationService.register(request));

            assertEquals("Username already exists", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("Đăng nhập thất bại khi username không tồn tại")
        void login_UsernameNotFound_ThrowsCustomException() {
            LoginRequest request = LoginRequest.builder()
                    .username("unknownuser")
                    .password("password123")
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

            CustomException exception = assertThrows(CustomException.class,
                    () -> authenticationService.login(request, httpServletResponse));

            assertEquals("Username not found", exception.getMessage());
        }

        @Test
        @DisplayName("Đăng nhập thất bại khi sai mật khẩu")
        void login_WrongPassword_ThrowsCustomException() {
            LoginRequest request = LoginRequest.builder()
                    .username("validuser")
                    .password("wrongpassword")
                    .build();

            User user = User.builder()
                    .username("validuser")
                    .password("encoded_correct_password")
                    .role(Role.ROLE_USER)
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

            CustomException exception = assertThrows(CustomException.class,
                    () -> authenticationService.login(request, httpServletResponse));

            assertEquals("Incorrect password", exception.getMessage());
        }

        @Test
        @DisplayName("Đăng nhập thành công trả về token")
        void login_ValidCredentials_ReturnsAuthResponse() {
            LoginRequest request = LoginRequest.builder()
                    .username("validuser")
                    .password("correctpassword")
                    .build();

            User user = User.builder()
                    .username("validuser")
                    .password("encoded_password")
                    .role(Role.ROLE_USER)
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
            when(jwtService.generateToken(anyString(), anyLong())).thenReturn("mock_token");

            AuthenticationResponse response = authenticationService.login(request, httpServletResponse);

            assertNotNull(response);
            verify(jwtService, atLeast(1)).generateToken(anyString(), anyLong());
        }
    }
}
