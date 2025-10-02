package SocialNetwork.SocialNetwork.config;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.entities.UserProvider;
import SocialNetwork.SocialNetwork.repositories.UserProviderRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;

    public CustomOAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository, UserProviderRepository userProviderRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userProviderRepository = userProviderRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String providerId = oAuth2User.getAttribute("sub");
        String providerName = oauthToken.getAuthorizedClientRegistrationId();
        String avatar = oAuth2User.getAttribute("picture");
        User user = userRepository.findByGmail(email);
        if (user == null) { 
            User newUser = new User();
            UserProvider userProvider = new UserProvider();
            userProvider.setProviderId(providerId);
            userProvider.setProviderName(providerName);
            userProviderRepository.save(userProvider);
            newUser.setDisplayname(name);
            newUser.setAvatar(avatar);
            newUser.setGmail(email);
            newUser.setStatus(1);
            newUser.setProvider(userProvider);
            userRepository.save(newUser);
        }
        String jwt = jwtService.generateToken(email);
        ResponseCookie cookie = ResponseCookie.from("accessToken", jwt)
        .httpOnly(true)      
        .secure(false)         
        .path("/")
        .sameSite("None")     
        .maxAge(Duration.ofHours(12))
        .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect("/home");
    }
}
