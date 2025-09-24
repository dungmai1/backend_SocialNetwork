package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
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
@RequestMapping("/like")
public class LikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private UserService userService;
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addLike(@RequestHeader("Authorization") String jwt,
                                               Integer PostId){
        try{
            User user = userService.findUserByJwt(jwt);
            likeService.addLike(PostId,user);
            return new ResponseEntity<>(new ApiResponse(true,"Like success"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/CountAllLikeForPost")
    public ResponseEntity allLikeForPost(Integer PostId) {
        try {
            Integer countLike = likeService.getAllLikesForPost(PostId);
            return ResponseEntity.ok(countLike);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @GetMapping("/AllUserLikePost")
    public List<User> getAllUserLikePost(Integer PostId){
        List<User> userList = likeService.getAllUserLikePost(PostId);
        return userList;
    }
}
