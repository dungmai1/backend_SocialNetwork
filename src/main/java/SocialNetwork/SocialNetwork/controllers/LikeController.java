package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.LikeService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
    @PostMapping("post/add")
    public ResponseEntity<ApiResponse> addLikePost(@CookieValue(value = "accessToken", required = false)  String jwt,
                                               Long postId){
        try{
            User user = userService.findUserByJwt(jwt);
            likeService.addLikePost(postId,user);
            return new ResponseEntity<>(new ApiResponse(true,"Like success"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("post/count")
    public ResponseEntity<?> allLikeForPost(@CookieValue(value = "accessToken", required = false) String token,
                                            Long postId) {
        try {
            User user = userService.findUserByJwt(token);
            Map<String, Object> result = new HashMap<>();
            boolean liked = likeService.hasUserLikedPost(postId, user.getId());
            Long countLike = likeService.getPostLikeCount(postId);
            result.put("liked", liked);
            result.put("likeCount", countLike);
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0L);
        }
    }
    @GetMapping("post/users")
    public List<UserDTO> getAllUserLikePost(Long postId){
        List<UserDTO> userList = likeService.getAllUserLikePost(postId);
        return userList;
    }
    @PostMapping("comment/add")
    public ResponseEntity<ApiResponse> addLikeComment(@CookieValue(value = "accessToken", required = false)  String jwt,
                                               Long commentId){
        try{
            User user = userService.findUserByJwt(jwt);
            likeService.addLikeComment(commentId,user);
            return new ResponseEntity<>(new ApiResponse(true,"Like success"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("comment/count")
    public ResponseEntity<?> allLikeForComment(@CookieValue(value = "accessToken", required = false) String token,
                                                    Long commentId) {
        try {
            User user = userService.findUserByJwt(token);
            Map<String, Object> result = new HashMap<>();
            boolean liked = likeService.hasUserLikedComment(commentId, user.getId());
            Long countLike = likeService.getCommentLikeCount(commentId);
            result.put("liked", liked);
            result.put("likeCount", countLike);
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0L);
        }
    }
    @GetMapping("comment/users")
    public List<UserDTO> getAllUserLikeComment(Long commentId){
        List<UserDTO> userList = likeService.getAllUserLikeComment(commentId);
        return userList;
    }
}
