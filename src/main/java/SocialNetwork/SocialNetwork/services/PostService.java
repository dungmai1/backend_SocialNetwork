package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.bindingModels.PostRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostDTO;

import java.util.List;
public interface PostService {
    public PostDTO createPost(PostRequest PostRequest, User user);
    public List<PostDTO> getAllPostsByUser(User user);
    public boolean deletePost(User user,Integer PostId);

    PostDTO getSinglePost(User user, Integer postId);
    public List<PostDTO> getAllPosts(User user,Integer status);

    public boolean savePost(User user, Integer postId);
    public List<PostDTO> GetAllSavedPost(User user);

    List<PostDTO> getAllPostsByUsername(String username);

    List<PostDTO> getAllPostsByImagePath(List<String> imagePaths);

    List<PostDTO> GetAllPostByFollowing(String username);

    void BanPost(Integer postId);

    void unbanPost(Integer postId);
    public List<PostDTO> getAllPostBan(User user,Integer status);

}
