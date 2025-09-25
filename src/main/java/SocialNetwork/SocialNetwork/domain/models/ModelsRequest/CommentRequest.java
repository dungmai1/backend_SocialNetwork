package SocialNetwork.SocialNetwork.domain.models.ModelsRequest;

import lombok.Data;

@Data
public class CommentRequest {
    private Integer postId;
    private String contentCmt;
    private String imageUrl;
}
