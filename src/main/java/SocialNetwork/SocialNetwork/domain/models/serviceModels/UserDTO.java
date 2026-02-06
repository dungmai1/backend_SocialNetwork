package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO implements Serializable {
    private Long id;
    private String username;
    private String displayname;
    private String avatar;
    private String gmail;
    private String phone;
    private String description;
}
