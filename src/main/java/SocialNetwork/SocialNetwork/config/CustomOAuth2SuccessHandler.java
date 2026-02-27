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

import SocialNetwork.SocialNetwork.domain.entities.Role;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.entities.UserProvider;
import SocialNetwork.SocialNetwork.repositories.UserProviderRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserProviderRepository userProviderRepository;
    
    @Value("${app.frontend.url}")
    private String frontendUrl;

    public CustomOAuth2SuccessHandler(JwtService jwtService, UserRepository userRepository,
            UserProviderRepository userProviderRepository) {
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
        User user = userRepository.findByEmail(email);
        if (user == null) {
            User newUser = new User();
            UserProvider userProvider = new UserProvider();
            userProvider.setProviderId(providerId);
            userProvider.setProviderName(providerName);
            userProviderRepository.save(userProvider);
            newUser.setDisplayname(name);
            newUser.setAvatar(avatar);
            newUser.setEmail(email);
            newUser.setUsername(email.split("@")[0]);
            newUser.setRole(Role.ROLE_USER);
            newUser.setStatus(1);
            newUser.setProvider(userProvider);
            userRepository.save(newUser);
        }
        String accessToken = jwtService.generateToken(email, 15);
        String refreshToken = jwtService.generateToken(email, 10080);
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/v1/auth/refresh")
                .sameSite("Lax")
                .maxAge(Duration.ofDays(7))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        response.sendRedirect(frontendUrl);
    }
}
