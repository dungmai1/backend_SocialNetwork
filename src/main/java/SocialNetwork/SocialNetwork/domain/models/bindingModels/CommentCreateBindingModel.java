package SocialNetwork.SocialNetwork.domain.models.bindingModels;

import lombok.Data;

@Data
public class CommentCreateBindingModel {
    private Integer PostId;
    private String content_cmt;
    private String ImageUrl;
}
