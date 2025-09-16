package SocialNetwork.SocialNetwork.domain.models.bindingModels;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class CommentCreateBindingModel {
    private Integer PostId;
    private String content_cmt;
    private String ImageUrl;
}
