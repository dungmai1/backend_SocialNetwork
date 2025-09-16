package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.RelationshipService;
import SocialNetwork.SocialNetwork.services.UserService;
import SocialNetwork.SocialNetwork.servicesImpl.RelationshipServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})

@RestController
@RequestMapping("/relationship")
public class RelationshipController {
    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private UserService userService;
    @PostMapping("/addFollow/{username}")
    public ResponseEntity<ApiResponse> addFriend(@RequestHeader("Authorization") String jwt,
                                                 @PathVariable String username){

        try {
            User user = userService.findUserByJwt(jwt);
            relationshipService.CreateRequestAddingFriend(user,username);
            return new ResponseEntity<>(new ApiResponse(true, "Create Request Adding Friend Success"),HttpStatus.OK);

        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false,e.getMessage()),HttpStatus.BAD_REQUEST);
        }
    }
//    @PostMapping("/acceptFriend")
//    public ResponseEntity<ApiResponse> acceptFriend(@RequestHeader("Authorization") String jwt,
//                                                    Integer friendId){
//        User user = userService.findUserByJwt(jwt);
//        relationshipService.acceptFriend(user,friendId);
//        return new ResponseEntity<>(new ApiResponse(true, "Accept Friend Success"),HttpStatus.OK);
//    }
//    @PostMapping("/cancelFriend")
//    public ResponseEntity<ApiResponse> cancelFriend(@RequestHeader("Authorization") String jwt,
//                                                    Integer friendId){
//        User user = userService.findUserByJwt(jwt);
//        relationshipService.cancelFriendRequest(user,friendId);
//        return new ResponseEntity<>(new ApiResponse(true, "Cancel Friend Success"),HttpStatus.OK);
//    }
//    @PostMapping("/removeFriend")
//    public ResponseEntity<ApiResponse> removeFriend(@RequestHeader("Authorization") String jwt,
//                                                    Integer friendId){
//        User user = userService.findUserByJwt(jwt);
//        relationshipService.removeFriend(user,friendId);
//        return new ResponseEntity<>(new ApiResponse(true, "Remove Friend Success"),HttpStatus.OK);
//    }
//    @GetMapping("/allFollowOfUser")
//    public List<User> allFollowOfUser(String phone){
//        List<User> users =  relationshipService.getAllFriendOfUser(phone);
//        return users;
//    }
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
    public Boolean CheckFollow(@RequestHeader("Authorization") String jwt,@PathVariable String username){
        User user = userService.findUserByJwt(jwt);
        boolean checkfollow = relationshipService.checkFollow(user,username);
        return checkfollow;
    }
}
