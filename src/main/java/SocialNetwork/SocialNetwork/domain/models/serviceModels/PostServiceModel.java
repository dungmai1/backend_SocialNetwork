package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostServiceModel {
    private Integer id;
    private String content;
    private LocalDateTime PostTime;
    private User user;
    private int status;
    private String ImageUrl;
    private List<Like> likeList;
    private List<Comment> commentList;
}
