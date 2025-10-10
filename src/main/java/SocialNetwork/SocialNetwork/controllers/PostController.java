package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.PostRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.PostService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createPost(@RequestBody PostRequest PostRequest,
                                                  @RequestHeader("Authorization") String jwt) {
        try{
            User user = userService.findUserByJwt(jwt);
            postService.createPost(PostRequest,user);
            return new ResponseEntity<>(new ApiResponse(true, "Post has been created"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deletePost(@RequestHeader("Authorization") String jwt,
                                                  Long PostId){
        try{
            User user = userService.findUserByJwt(jwt);
            postService.deletePost(user,PostId);
            return new ResponseEntity<>(new ApiResponse(true, "Delete Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/GetAllPostByUser")
    public List<PostDTO> getAllPostByUser(@CookieValue(value = "accessToken", required = false) String token){
        User user = userService.findUserByJwt(token);
        List<PostDTO> PostDTOList = postService.getAllPostsByUser(user);
        return PostDTOList;
    }
    @GetMapping("/GetSinglePost")
    public PostDTO getSinglePost(@RequestHeader("Authorization") String jwt,
                                          Long PostId){
        User user = userService.findUserByJwt(jwt);
        PostDTO PostDTO = postService.getSinglePost(user,PostId);
        return PostDTO;
    }
    @GetMapping("/GetAllPost")
    public List<PostDTO> getAllPost(@CookieValue(value = "accessToken", required = false) String token){
        User user = userService.findUserByJwt(token);
        List<PostDTO> PostDTOList = postService.getAllPosts(user,1);
        return PostDTOList;
    }
    @PostMapping("/Save")
    public ResponseEntity<ApiResponse> savePost(Long PostId,@RequestHeader("Authorization") String jwt) {
        try{
            User user = userService.findUserByJwt(jwt);
            postService.savePost(user,PostId);
            return new ResponseEntity<>(new ApiResponse(true, "Save Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/GetAllSavedPost")
    public List<PostDTO> GetAllSavedPost(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        List<PostDTO> PostDTOList = postService.GetAllSavedPost(user);
        return PostDTOList;
    }
    @GetMapping("/GetAllPostByUsername/{username}")
    public List<PostDTO> getAllPostsByUsername(@PathVariable String username){
        List<PostDTO> PostDTOList = postService.getAllPostsByUsername(username);
        return PostDTOList;
    }
    @GetMapping("/GetAllPostByImagePath")
    public List<PostDTO> getAllPostsByImagePath(@RequestParam List<String> imagePaths){
        List<PostDTO> PostDTOList = postService.getAllPostsByImagePath(imagePaths);
        return PostDTOList;
    }
    @GetMapping("/GetAllPostByFollowing/{username}")
    public List<PostDTO> GetAllPostByFollowing(@PathVariable String username){
        List<PostDTO> PostDTOList = postService.GetAllPostByFollowing(username);
        return PostDTOList;
    }
    @PutMapping("/banPost")
    public ResponseEntity<ApiResponse> banPost(Long postId){
        try{
            postService.BanPost(postId);
            return new ResponseEntity<>(new ApiResponse(true, "Ban Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @PutMapping("/unbanPost")
    public ResponseEntity<ApiResponse> unbanPost(Long postId){
        try{
            postService.unbanPost(postId);
            return new ResponseEntity<>(new ApiResponse(true, "UnBan Post success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAllPostBan")
    public List<PostDTO> getAllPostBan(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserByJwt(jwt);
        List<PostDTO> PostDTOList = postService.getAllPostBan(user,2);
        return PostDTOList;
    }
}
