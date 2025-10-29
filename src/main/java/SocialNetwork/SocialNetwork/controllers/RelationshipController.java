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

    @PostMapping("/addFollow/{username}")
    public ResponseEntity<ApiResponse> addFollow(@CookieValue(value = "accessToken", required = false) String jwt,
            @PathVariable String username) {

        try {
            User user = userService.findUserByJwt(jwt);
            relationshipService.addFollow(user, username);
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
}
