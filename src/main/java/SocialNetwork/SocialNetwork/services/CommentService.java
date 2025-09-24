package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.CommentRequest;

import java.util.List;

public interface CommentService {
    boolean addComment(CommentRequest CommentRequest, User user);
    int CountAllCommentsForPost(Integer postId);

    void deleteComment(User user, Integer postId, Integer commentId);
    List<Comment> getAllCommentForPost(Integer postId);
}
