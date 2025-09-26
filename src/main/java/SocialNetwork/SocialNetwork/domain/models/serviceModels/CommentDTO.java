package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private Long postId;
    private String userDisplayname;
    private String userAvatar;
    private String imageUrl;
    private LocalDateTime commentTime;
}
