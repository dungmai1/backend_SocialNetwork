package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.bindingModels.PostCreateBindingModel;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostServiceModel;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.PostService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createPost(@RequestBody PostCreateBindingModel postCreateBindingModel,
                                                  @RequestHeader("Authorization") String jwt) {
        try{
            User user = userService.findUserByJwt(jwt);
            postService.createPost(postCreateBindingModel,user);
            return new ResponseEntity<>(new ApiResponse(true, "Post has been created"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deletePost(@RequestHeader("Authorization") String jwt,
                                                  Integer PostId){
        try{
            User user = userService.findUserByJwt(jwt);
            postService.deletePost(user,PostId);
            return new ResponseEntity<>(new ApiResponse(true, "Delete Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/GetAllPostByUser")
    public List<PostServiceModel> getAllPostByUser(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        List<PostServiceModel> postServiceModelList = postService.getAllPostsByUser(user);
        return postServiceModelList;
    }
    @GetMapping("/GetSinglePost")
    public PostServiceModel getSinglePost(@RequestHeader("Authorization") String jwt,
                                          Integer PostId){
        User user = userService.findUserByJwt(jwt);
        PostServiceModel postServiceModel = postService.getSinglePost(user,PostId);
        return postServiceModel;
    }
    @GetMapping("/GetAllPost")
    public List<PostServiceModel> getAllPost(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        List<PostServiceModel> postServiceModelList = postService.getAllPosts(user,1);
        return postServiceModelList;
    }
    @PostMapping("/Save")
    public ResponseEntity<ApiResponse> savePost(Integer PostId,@RequestHeader("Authorization") String jwt) {
        try{
            User user = userService.findUserByJwt(jwt);
            postService.savePost(user,PostId);
            return new ResponseEntity<>(new ApiResponse(true, "Save Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/GetAllSavedPost")
    public List<PostServiceModel> GetAllSavedPost(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        List<PostServiceModel> postServiceModelList = postService.GetAllSavedPost(user);
        return postServiceModelList;
    }
    @GetMapping("/GetAllPostByUsername/{username}")
    public List<PostServiceModel> getAllPostsByUsername(@PathVariable String username){
        List<PostServiceModel> postServiceModelList = postService.getAllPostsByUsername(username);
        return postServiceModelList;
    }
    @GetMapping("/GetAllPostByImagePath")
    public List<PostServiceModel> getAllPostsByImagePath(@RequestParam List<String> imagePaths){
        List<PostServiceModel> postServiceModelList = postService.getAllPostsByImagePath(imagePaths);
        return postServiceModelList;
    }
    @GetMapping("/GetAllPostByFollowing/{username}")
    public List<PostServiceModel> GetAllPostByFollowing(@PathVariable String username){
        List<PostServiceModel> postServiceModelList = postService.GetAllPostByFollowing(username);
        return postServiceModelList;
    }
    @PutMapping("/banPost")
    public ResponseEntity<ApiResponse> banPost(Integer postId){
        try{
            postService.BanPost(postId);
            return new ResponseEntity<>(new ApiResponse(true, "Ban Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/unbanPost")
    public ResponseEntity<ApiResponse> unbanPost(Integer postId){
        try{
            postService.unbanPost(postId);
            return new ResponseEntity<>(new ApiResponse(true, "UnBan Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAllPostBan")
    public List<PostServiceModel> getAllPostBan(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        List<PostServiceModel> postServiceModelList = postService.getAllPostBan(user,2);
        return postServiceModelList;
    }
}
