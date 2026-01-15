package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO implements Serializable {
    private Long id;
    private String content;
    private Long postId;
    private String username;
    private String userAvatar;
    private String imageUrl;
    private LocalDateTime commentTime;
    private Long parentId;
}
