package SocialNetwork.SocialNetwork.services;


import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;

import java.util.List;

public interface LikeService {
    boolean addLikePost(Long postId, User user);
    Long getPostLikeCount(Long postId);
    List<UserDTO> getAllUserLikePost(Long postId);
    boolean addLikeComment(Long commentId, User user);
    Long getCommentLikeCount(Long commentId);
    List<UserDTO> getAllUserLikeComment(Long commentId);
}
