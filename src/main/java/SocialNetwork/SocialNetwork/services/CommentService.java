package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.CommentRequest;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.RepCommentRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CommentDTO;

import java.util.List;

public interface CommentService {
    CommentDTO addComment(CommentRequest CommentRequest, User user);
    int CountAllCommentsForPost(Long postId);
    void deleteComment(User user, Long postId, Long commentId);
    List<CommentDTO> getAllCommentForPost(Long postId);
    CommentDTO addRepComment(RepCommentRequest repCommentRequest, User user);
    int CountAllRepComment(Long commentId);
    List<CommentDTO> getAllRepComment(Long commentId);
}
