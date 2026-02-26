package SocialNetwork.SocialNetwork.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
    }

    @Test
    @DisplayName("Tạo token thành công với username hợp lệ")
    void generateToken_ValidUsername_ReturnsNonNullToken() {
        String token = jwtService.generateToken("testuser", 15);
        assertNotNull(token, "Token không được null");
        assertFalse(token.isBlank(), "Token không được rỗng");
    }

    @Test
    @DisplayName("Validate token hợp lệ trả về đúng username")
    void validateToken_ValidToken_ReturnsCorrectUsername() {
        String username = "john_doe";
        String token = jwtService.generateToken(username, 15);

        String result = jwtService.validateToken(token);

        assertEquals(username, result, "Username từ token phải khớp với username gốc");
    }

    @Test
    @DisplayName("Validate token hết hạn trả về null")
    void validateToken_ExpiredToken_ReturnsNull() {
        String token = jwtService.generateToken("expireduser", 0);
        assertDoesNotThrow(() -> jwtService.validateToken(token),
                "validateToken không được ném exception với token hết hạn");
    }

    @Test
    @DisplayName("Validate token không hợp lệ (chuỗi rác) trả về null")
    void validateToken_InvalidToken_ReturnsNull() {
        String result = jwtService.validateToken("invalid.token.string");
        assertNull(result, "Token không hợp lệ phải trả về null");
    }

    @Test
    @DisplayName("Validate token null trả về null")
    void validateToken_NullToken_ReturnsNull() {
        String result = jwtService.validateToken(null);
        assertNull(result, "Token null phải trả về null");
    }

    @ParameterizedTest
    @ValueSource(strings = {"user1", "admin", "test_user_123"})
    @DisplayName("Tạo và validate token với nhiều username khác nhau")
    void generateAndValidateToken_MultipleUsernames_WorksCorrectly(String username) {
        String token = jwtService.generateToken(username, 60);
        String validated = jwtService.validateToken(token);
        assertEquals(username, validated);
    }

    @Test
    @DisplayName("Token có đúng 3 phần phân tách bởi dấu chấm (JWT format)")
    void generateToken_ValidFormat_HasThreePartsAndIsValid() {
        String token = jwtService.generateToken("sameuser", 15);
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT phải có đúng 3 phần: header.payload.signature");
        assertEquals("sameuser", jwtService.validateToken(token));
    }
}
