package SocialNetwork.SocialNetwork.domain.models.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String username;
    private String displayname;
    private String email;
    private String phonenumber;
    private String bio;
}
