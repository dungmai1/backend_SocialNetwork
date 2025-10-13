package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.RelationshipService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})

@RestController
@RequestMapping("/relationship")
public class RelationshipController {
    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private UserService userService;
    @PostMapping("/addFollow/{userId}")
    public ResponseEntity<ApiResponse> addFollow(@CookieValue(value = "accessToken", required = false)  String jwt,
                                                 @PathVariable Long userId){

        try {
            User user = userService.findUserByJwt(jwt);
            relationshipService.addFollow(user,userId);
            return new ResponseEntity<>(new ApiResponse(true, "Create Request Adding Follow Success"),HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false,e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/following/{username}")
    public List<User> Following(@PathVariable String username){
        List<User> users =  relationshipService.getFollowing(username);
        return users;
    }
    @GetMapping("/followers/{username}")
    public List<User> Followers(@PathVariable String username){
        List<User> users =  relationshipService.getFollower(username);
        return users;
    }
    @GetMapping("/checkfollow/{username}")
    public Boolean CheckFollow(@CookieValue(value = "accessToken", required = false)  String jwt,@PathVariable String username){
        User user = userService.findUserByJwt(jwt);
        boolean checkfollow = relationshipService.checkFollow(user,username);
        return checkfollow;
    }
    @GetMapping("count/following/{userId}")
    public Long countFollowing(@PathVariable Long userId){
        return relationshipService.countFollowing(userId);
    }
    @GetMapping("count/followers/{userId}")
    public Long countFollowers(@PathVariable Long userId){
        return relationshipService.countFollower(userId);
    }
}
