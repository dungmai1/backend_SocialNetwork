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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })

@RestController
@RequestMapping("/relationship")
public class RelationshipController {
    @Autowired
    private RelationshipService relationshipService;
    @Autowired
    private UserService userService;

    @PostMapping("/addFollow/{userId}")
    public ResponseEntity<ApiResponse> addFollow(@CookieValue(value = "accessToken", required = false) String jwt,
            @PathVariable Long userId) {

        try {
            User user = userService.findUserByJwt(jwt);
            relationshipService.addFollow(user, userId);
            return new ResponseEntity<>(new ApiResponse(true, "Create Request Adding Follow Success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/following/{username}")
    public List<User> Following(@PathVariable String username) {
        List<User> users = relationshipService.getFollowing(username);
        return users;
    }

    @GetMapping("/followers/{username}")
    public List<User> Followers(@PathVariable String username) {
        List<User> users = relationshipService.getFollower(username);
        return users;
    }

    @GetMapping("/checkfollow/{username}")
    public Boolean CheckFollow(@CookieValue(value = "accessToken", required = false) String jwt,
            @PathVariable String username) {
        User user = userService.findUserByJwt(jwt);
        boolean checkfollow = relationshipService.checkFollow(user, username);
        return checkfollow;
    }

    @GetMapping("count/following/{username}")
    public ResponseEntity<?> countFollowing(@CookieValue(value = "accessToken", required = false) String jwt,
            @PathVariable String username) {
        User currentUser = userService.findUserByJwt(jwt);
        User targetUser = userService.findUserByUsername(username);
        Map<String, Object> result = new HashMap<>();
        Long following = relationshipService.countFollowing(targetUser.getId());
        boolean isFollowing = relationshipService.checkFollow(currentUser, targetUser.getUsername());
        result.put("countFollowing", following);
        result.put("isFollowing", isFollowing);
        return ResponseEntity.ok(result);
    }

    @GetMapping("count/followers/{username}")
    public ResponseEntity<?> countFollowers(@CookieValue(value = "accessToken", required = false) String jwt,
            @PathVariable String username) {
        User currentUser = userService.findUserByJwt(jwt);
        User targetUser = userService.findUserByUsername(username);
        Map<String, Object> result = new HashMap<>();
        Long followers = relationshipService.countFollower(targetUser.getId());
        boolean isFollower = relationshipService.checkFollow(currentUser, targetUser.getUsername());
        result.put("countFollower", followers);
        result.put("isFollower", isFollower);
        return ResponseEntity.ok(result);
    }
}
