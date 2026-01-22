package SocialNetwork.SocialNetwork.domain.models.ModelsRequest;

import lombok.Data;

@Data
public class EditCommentRequest {
    private Long postId;
    private Long commentId;
    private String content;
    private String imageUrl;
}
