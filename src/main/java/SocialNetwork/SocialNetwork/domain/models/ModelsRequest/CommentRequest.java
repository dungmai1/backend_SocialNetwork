package SocialNetwork.SocialNetwork.domain.models.ModelsRequest;

import lombok.Data;

@Data
public class CommentRequest {
    private Long postId;
    private String contentCmt;
    private String imageUrl;
}
