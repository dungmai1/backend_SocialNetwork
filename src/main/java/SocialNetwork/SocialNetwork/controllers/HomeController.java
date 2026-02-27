package SocialNetwork.SocialNetwork.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @GetMapping("/login_google")
    public String index() {
        return "Welcome! <a href='/oauth2/authorization/google'>Login with Google</a>";
    }

    @GetMapping("/check")
    public String check() {
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        return "OK";
    }
}
