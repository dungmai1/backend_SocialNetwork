package SocialNetwork.SocialNetwork.domain.models.ModelsRequest;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String displayname;
    private String phone;
    private String email;
    private String description;
}
