package SocialNetwork.SocialNetwork.config;

import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    
    private final JwtAuthFilter jwtAuthFilter;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    public SecurityConfiguration (JwtAuthFilter jwtAuthFilter, CustomOAuth2SuccessHandler customOAuth2SuccessHandler){
        this.jwtAuthFilter = jwtAuthFilter;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable) 
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui*/**", "/", "/v3/api-docs/**","/api/v1/auth/**","/login_google", "/api/upload/image").permitAll()
                        .requestMatchers("/like/CountAllLikeForPost/**",
                                "/like/AllUserLikePost/**",
                                "/comment/CountAllCommentForPost",
                                "/user/**",
                                "/post/GetAllPostByUsername/**",
                                "/relationship/**"
                                    ).permitAll()
                        .requestMatchers("/post/banPost",
                                "/post/unbanPost",
                                "/post/getAllPostBan",
                                "/user/banUser",
                                "/user/unbanUser",
                                "/like/CountAllLike",
                                "/comment/countAllComment").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                    .loginPage("/login_google")
                    .successHandler(customOAuth2SuccessHandler)
                ) 
                .exceptionHandling(ex -> ex
                    .authenticationEntryPoint((req, res, exx) -> {
                        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        res.setContentType("application/json");
                        res.getWriter().write("{\"error\": \"Unauthorized\"}");
                    })
                ) 
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOriginPattern("http://localhost:3000");
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
