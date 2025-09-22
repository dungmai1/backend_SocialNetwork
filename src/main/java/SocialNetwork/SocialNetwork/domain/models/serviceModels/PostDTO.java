package SocialNetwork.SocialNetwork.domain.models.serviceModels;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Integer id;
    private String content;
    private LocalDateTime postTime;
    private String displayName;
    private String avatar;
    private int status;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
}
