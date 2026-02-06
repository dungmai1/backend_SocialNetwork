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
    @NotNull(message = "Username cannot be null")
    @Pattern(regexp = "^[a-zA-Z0-9._]{3,15}$", message = "Username must be 3-15 characters long and can only contain letters, numbers, underscores and dots")
    private String username;
    private String displayname;
    @NotNull(message = "Email cannot be null")
    @jakarta.validation.constraints.Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "Password cannot be null")
    @Pattern(regexp = "^.{6,20}$", message = "Password must be 6-20 characters long")
    private String password;
}
