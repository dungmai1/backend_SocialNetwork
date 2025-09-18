package SocialNetwork.SocialNetwork.auth;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull (message = "Username cannot be null")
    @Pattern (regexp = "^[a-zA-Z0-9_]{3,15}$", message = "Username must be 3-15 characters long and can only contain letters, numbers, and underscores")
    private String username;
    private String displayname;
    @Pattern(regexp = "^[0-9]{10,}$", message = "Phone number must contain at least 10 digits")
    private String phone;
    @NotNull (message = "Password cannot be null")
    @Pattern (regexp = "^[a-zA-Z0-9_]{6,20}$", message = "Password must be 6-20 characters long and can only contain letters, numbers, and underscores")
    private String password;
}
