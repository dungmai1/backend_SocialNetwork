package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.config.JwtService;
import SocialNetwork.SocialNetwork.domain.entities.Role;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserProfileDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.RelationshipRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private ModelMapper modelMapper;

    private UserServiceImpl userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, jwtService, modelMapper);
        ReflectionTestUtils.setField(userService, "jwtService", jwtService);
        ReflectionTestUtils.setField(userService, "relationshipRepository", relationshipRepository);

        mockUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .displayname("Test User")
                .avatar("https://example.com/avatar.png")
                .role(Role.ROLE_USER)
                .status(1)
                .build();
    }

    @Nested
    @DisplayName("findUserByJwt()")
    class FindUserByJwtTests {

        @Test
        @DisplayName("Tìm user thành công với token hợp lệ")
        void findUserByJwt_ValidToken_ReturnsUser() {
            when(jwtService.validateToken("valid_token")).thenReturn("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

            User result = userService.findUserByJwt("valid_token");

            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("Ném CustomException khi token không hợp lệ")
        void findUserByJwt_InvalidToken_ThrowsCustomException() {
            when(jwtService.validateToken("bad_token")).thenReturn(null);

            CustomException ex = assertThrows(CustomException.class,
                    () -> userService.findUserByJwt("bad_token"));

            assertEquals("Invalid JWT token", ex.getMessage());
        }

        @Test
        @DisplayName("Ném CustomException khi user không tồn tại")
        void findUserByJwt_UserNotFound_ThrowsCustomException() {
            when(jwtService.validateToken("valid_token")).thenReturn("ghostuser");
            when(userRepository.findByUsername("ghostuser")).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> userService.findUserByJwt("valid_token"));

            assertEquals("User not found for token", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("findUser()")
    class FindUserTests {

        @Test
        @DisplayName("Trả về UserDTO với token hợp lệ")
        void findUser_ValidToken_ReturnsUserDTO() {
            UserDTO dto = new UserDTO();
            when(jwtService.validateToken("valid_token")).thenReturn("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));
            when(modelMapper.map(mockUser, UserDTO.class)).thenReturn(dto);

            UserDTO result = userService.findUser("valid_token");

            assertNotNull(result);
        }

        @Test
        @DisplayName("Ném CustomException khi token null")
        void findUser_InvalidToken_ThrowsCustomException() {
            when(jwtService.validateToken("invalid")).thenReturn(null);

            assertThrows(CustomException.class, () -> userService.findUser("invalid"));
        }
    }

    @Nested
    @DisplayName("searchUserName()")
    class SearchUsernameTests {

        @Test
        @DisplayName("Tìm kiếm trả về danh sách user khớp")
        void searchUserName_MatchFound_ReturnsList() {
            List<User> users = Arrays.asList(mockUser);
            when(userRepository.findByUsernameContaining("test")).thenReturn(users);

            List<User> result = userService.searchUserName("test");

            assertEquals(1, result.size());
            assertEquals("testuser", result.get(0).getUsername());
        }

        @Test
        @DisplayName("Tìm kiếm trả về list rỗng khi không có kết quả")
        void searchUserName_NoMatch_ReturnsEmptyList() {
            when(userRepository.findByUsernameContaining("xyz")).thenReturn(List.of());

            List<User> result = userService.searchUserName("xyz");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Tìm kiếm repository trả về null thì trả về list rỗng")
        void searchUserName_RepositoryReturnsNull_ReturnsEmptyList() {
            when(userRepository.findByUsernameContaining("null_case")).thenReturn(null);

            List<User> result = userService.searchUserName("null_case");

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("getAllUser()")
    class GetAllUserTests {

        @Test
        @DisplayName("Trả về tất cả users từ repository")
        void getAllUser_ReturnsAllUsers() {
            List<User> users = Arrays.asList(mockUser,
                    User.builder().id(2L).username("user2").build());
            when(userRepository.findAll()).thenReturn(users);

            List<User> result = userService.getAllUser();

            assertEquals(2, result.size());
        }
    }

    @Nested
    @DisplayName("findUserByUsername()")
    class FindUserByUsernameTests {

        @Test
        @DisplayName("Ném CustomException khi username không tồn tại")
        void findUserByUsername_NotFound_ThrowsCustomException() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            CustomException ex = assertThrows(CustomException.class,
                    () -> userService.findUserByUsername(mockUser, "ghost"));

            assertEquals("User not found", ex.getMessage());
        }

        @Test
        @DisplayName("Trả về UserProfileDTO khi username tồn tại")
        void findUserByUsername_Found_ReturnsUserProfileDTO() {
            User targetUser = User.builder().id(2L).username("targetuser").build();
            when(userRepository.findByUsername("targetuser")).thenReturn(Optional.of(targetUser));
            when(relationshipRepository.countFollower(targetUser.getId())).thenReturn(10L);
            when(relationshipRepository.countFollowing(targetUser.getId())).thenReturn(5L);
            when(relationshipRepository.existsByUserOneAndUserTwo(anyLong(), anyLong())).thenReturn(false);
            when(relationshipRepository.existsByUserTwoAndUserOne(anyLong(), anyLong())).thenReturn(true);

            UserProfileDTO result = userService.findUserByUsername(mockUser, "targetuser");

            assertNotNull(result);
            assertEquals("targetuser", result.getUsername());
        }
    }
}
