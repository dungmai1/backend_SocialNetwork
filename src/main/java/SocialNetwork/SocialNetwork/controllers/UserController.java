package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserProfileDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import SocialNetwork.SocialNetwork.domain.models.requests.UserUpdateRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public UserDTO getUser(@CookieValue(value = "accessToken", required = false) String token) {
        UserDTO user = userService.findUser(token);
        return user;
    }

    @GetMapping("/{username}")
    public UserProfileDTO getUserByUsername(@CookieValue(value = "accessToken", required = false) String token,
            @PathVariable String username) {
        User user = userService.findUserByJwt(token);
        UserProfileDTO userProfileDTO = userService.findUserByUsername(user, username);
        return userProfileDTO;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(
            @CookieValue(value = "accessToken", required = false) String jwt,
            @RequestParam(required = false, defaultValue = "") String textSearch) {
        try {
            // Xác thực user đã đăng nhập
            userService.findUserByJwt(jwt);

            // Validate input
            if (textSearch.trim().isEmpty()) {
                return new ResponseEntity<>(new ApiResponse(false, "Search text is required"), HttpStatus.BAD_REQUEST);
            }

            List<User> users = userService.searchUserName(textSearch.trim());
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getAllUser")
    public List<User> getAllUser(@CookieValue(value = "accessToken", required = false) String jwt) {
        List<User> users = userService.getAllUser();
        return users;
    }

    @PutMapping("/updateUser")
    public ResponseEntity<ApiResponse> updateUser(
            @CookieValue(value = "accessToken", required = false) String jwt,
            @ModelAttribute UserUpdateRequest request,
            @RequestParam(value = "avatar", required = false) MultipartFile avatar) {
        try {
            User user = userService.findUserByJwt(jwt);
            userService.updateUser(user, request, avatar);
            return new ResponseEntity<>(new ApiResponse(true, "Update User success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/banUser")
    public ResponseEntity<ApiResponse> banUser(Integer UserId) {
        try {
            userService.banUser(UserId);
            return new ResponseEntity<>(new ApiResponse(true, "Ban User success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/unbanUser")
    public ResponseEntity<ApiResponse> UnbanUser(Integer UserId) {
        try {
            userService.UnbanUser(UserId);
            return new ResponseEntity<>(new ApiResponse(true, "UnBan User success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
