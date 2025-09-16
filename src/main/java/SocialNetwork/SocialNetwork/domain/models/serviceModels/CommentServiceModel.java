package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentServiceModel {
    private Integer Id;
    private String content_cmt;
    private User user;
    private Post post;
    private String ImageUrl;
    private LocalDateTime CommentTime;
}
