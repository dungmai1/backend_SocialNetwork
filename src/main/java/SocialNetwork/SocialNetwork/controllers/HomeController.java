package SocialNetwork.SocialNetwork.controllers;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("/login_google")
    public String index() {
        return "Welcome! <a href='/oauth2/authorization/google'>Login with Google</a>";
    }

    @GetMapping("/home")
    public String home(OAuth2AuthenticationToken authentication) {
        return "Hello " + authentication.getPrincipal().getAttributes().get("name");
    }
}
