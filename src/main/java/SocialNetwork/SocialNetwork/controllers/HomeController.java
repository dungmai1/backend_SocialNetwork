package SocialNetwork.SocialNetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.UserService;

@RestController
public class HomeController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private UserService userService;

    @GetMapping("/login_google")
    public String index() {
        return "Welcome! <a href='/oauth2/authorization/google'>Login with Google</a>";
    }

    @GetMapping("/check")
    public String check() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return "OK";
    }

    @GetMapping("/api/me")
    public ResponseEntity<?> me(@CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok("OK");
        } catch (CustomException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
