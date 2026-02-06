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
public class ResetPasswordRequest {
    @NotNull(message = "Token cannot be null")
    private String token;
    @NotNull(message = "New password cannot be null")
    @Pattern(regexp = "^.{6,20}$", message = "Password must be 6-20 characters long")
    private String newPassword;
}
