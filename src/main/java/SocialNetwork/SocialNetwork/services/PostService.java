package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.PostRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CursorResponse;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostDTO;

import java.time.LocalDateTime;
import java.util.List;
public interface PostService {
    public PostDTO createPost(PostRequest PostRequest, User user);
    public List<PostDTO> getAllPostsByUser(User user);
    public boolean deletePost(User user, Long PostId);

    PostDTO getSinglePost(User user, Long postId);
    public CursorResponse<PostDTO> getAllPosts(LocalDateTime cursorTime, User user, int limit);

    public boolean savePost(User user, Long postId);
    public List<PostDTO> GetAllSavedPost(User user);

    List<PostDTO> getAllPostsByUsername(String username);

    List<PostDTO> getAllPostsByImagePath(List<String> imagePaths);

    List<PostDTO> GetAllPostByFollowing(String username);

    void BanPost(Long postId);

    void unbanPost(Long postId);
    public List<PostDTO> getAllPostBan(User user);
    List<PostDTO> getAllSavedPostsByUsername(String username);


}
