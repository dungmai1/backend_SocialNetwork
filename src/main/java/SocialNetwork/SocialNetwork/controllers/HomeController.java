package SocialNetwork.SocialNetwork.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/login_google")
    public String index() {
        return "Welcome! <a href='/oauth2/authorization/google'>Login with Google</a>";
    }

    @GetMapping("/home")
    public String home() {
        return "Hello Dung Mai";
    }
}
