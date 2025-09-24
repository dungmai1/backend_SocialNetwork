package SocialNetwork.SocialNetwork.domain.models.ModelsRequest;

import lombok.Data;

@Data
public class CommentRequest {
    private Integer PostId;
    private String content_cmt;
    private String ImageUrl;
}
