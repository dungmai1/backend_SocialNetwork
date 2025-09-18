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
public class LoginRequest {
    @NotNull (message = "Username cannot be null")
    @Pattern (regexp = "^[a-zA-Z0-9_]{3,15}$", message = "Username must be 3-15 characters long and can only contain letters, numbers, and underscores")
    private String username;
    @NotNull (message = "Password cannot be null")
    @Pattern (regexp = "^[a-zA-Z0-9_]{6,20}$", message = "Password must be 6-20 characters long and can only contain letters, numbers, and underscores")
    private String password;
}
