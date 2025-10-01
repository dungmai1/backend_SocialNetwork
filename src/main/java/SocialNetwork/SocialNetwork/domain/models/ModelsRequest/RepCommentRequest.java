package SocialNetwork.SocialNetwork.domain.models.ModelsRequest;

import lombok.Data;

@Data
public class RepCommentRequest {
    private Long commentId;
    private String content;
    private String imageUrl;
}
