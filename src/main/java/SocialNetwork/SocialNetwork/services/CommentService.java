package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.bindingModels.CommentCreateBindingModel;

import java.util.List;

public interface CommentService {
    boolean addComment(CommentCreateBindingModel commentCreateBindingModel, User user);
    int CountAllCommentsForPost(Integer PostID);

    void deleteComment(User user, Integer postId, Integer commentId);
    List<Comment> getAllCommentForPost(Integer PostID);

    Integer countAllComment();
}
