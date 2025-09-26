package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.CommentRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CommentDTO;

import java.util.List;

public interface CommentService {
    boolean addComment(CommentRequest CommentRequest, User user);
    int CountAllCommentsForPost(Long postId);

    void deleteComment(User user, Long postId, Long commentId);
    List<CommentDTO> getAllCommentForPost(Long postId);
}
