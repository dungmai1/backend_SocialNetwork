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

import java.util.List;
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})

@RestController
@RequestMapping("/likes")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addLike(@RequestHeader("Authorization") String jwt,
                                               Long postId){
        try{
            User user = userService.findUserByJwt(jwt);
            likeService.addLikePost(postId,user);
            return new ResponseEntity<>(new ApiResponse(true,"Like success"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/count")
    public ResponseEntity<Long> allLikeForPost(Long postId) {
        try {
            Long countLike = likeService.getPostLikeCount(postId);
            return ResponseEntity.ok(countLike);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0L);
        }
    }
    @GetMapping("/users")
    public List<UserDTO> getAllUserLikePost(Long postId){
        List<UserDTO> userList = likeService.getAllUserLikePost(postId);
        return userList;
    }
}
