package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.PostRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CursorResponse;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.PostService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            postService.createPost(PostRequest, user);
            return new ResponseEntity<>(new ApiResponse(true, "Post has been created"), HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deletePost(@CookieValue(value = "accessToken", required = false) String jwt,
            Long PostId) {
        try {
            User user = userService.findUserByJwt(jwt);
            postService.deletePost(user, PostId);
            return new ResponseEntity<>(new ApiResponse(true, "Delete Post success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/GetSinglePost")
    public PostDTO getSinglePost(@CookieValue(value = "accessToken", required = false) String jwt,
            Long PostId) {
        User user = userService.findUserByJwt(jwt);
        PostDTO PostDTO = postService.getSinglePost(user, PostId);
        return PostDTO;
    }

    @GetMapping("/GetAllPost")
    public CursorResponse<PostDTO> getAllPost(@CookieValue(value = "accessToken", required = false) String token,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime cursor,
            @RequestParam(defaultValue = "10") int limit) {
        User user = userService.findUserByJwt(token);
        CursorResponse<PostDTO> PostDTOList = postService.getAllPosts(cursor, user, limit);
        return PostDTOList;
    }

    @PostMapping("/Save")
    public ResponseEntity<ApiResponse> savePost(Long PostId,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            postService.savePost(user, PostId);
            return new ResponseEntity<>(new ApiResponse(true, "Save Post success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/GetAllSavedPost")
    public List<PostDTO> GetAllSavedPost(@CookieValue(value = "accessToken", required = false) String jwt) {
        User user = userService.findUserByJwt(jwt);
        List<PostDTO> PostDTOList = postService.GetAllSavedPost(user);
        return PostDTOList;
    }

    @GetMapping("/GetAllPostByUsername/{username}")
    public List<PostDTO> getAllPostsByUsername(@PathVariable String username) {
        List<PostDTO> PostDTOList = postService.getAllPostsByUsername(username);
        return PostDTOList;
    }

    @GetMapping("/GetAllPostByImagePath")
    public List<PostDTO> getAllPostsByImagePath(@RequestParam List<String> imagePaths) {
        List<PostDTO> PostDTOList = postService.getAllPostsByImagePath(imagePaths);
        return PostDTOList;
    }

    @GetMapping("/GetAllPostByFollowing/{username}")
    public List<PostDTO> GetAllPostByFollowing(@PathVariable String username) {
        List<PostDTO> PostDTOList = postService.GetAllPostByFollowing(username);
        return PostDTOList;
    }

    @PutMapping("/banPost")
    public ResponseEntity<ApiResponse> banPost(Long postId) {
        try {
            postService.BanPost(postId);
            return new ResponseEntity<>(new ApiResponse(true, "Ban Post success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/unbanPost")
    public ResponseEntity<ApiResponse> unbanPost(Long postId) {
        try {
            postService.unbanPost(postId);
            return new ResponseEntity<>(new ApiResponse(true, "UnBan Post success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllPostBan")
    public List<PostDTO> getAllPostBan(@CookieValue(value = "accessToken", required = false) String jwt) {
        User user = userService.findUserByJwt(jwt);
        List<PostDTO> PostDTOList = postService.getAllPostBan(user);
        return PostDTOList;
    }
}
