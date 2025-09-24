package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Integer Id;
    private String content_cmt;
    private User user;
    private Post post;
    private String ImageUrl;
    private LocalDateTime CommentTime;
}
