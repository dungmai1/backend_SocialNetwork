package SocialNetwork.SocialNetwork.domain.models.serviceModels;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {
    private Integer id;
    private String content;
    private LocalDateTime postTime;
    private String username;
    private String avatar;
    private int status;
    private List<String> images;
    private int likeCount;
    private int commentCount;
    private boolean isSaved;
}
