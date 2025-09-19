package SocialNetwork.SocialNetwork.config;

import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    
    private final JwtAuthFilter jwtAuthFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable) 
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui*/**", "/", "/v3/api-docs/**","/api/v1/auth/**","/login_google","/home").permitAll()
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
                // .oauth2Login(oauth2 -> oauth2
                //         .loginPage("/login_google")
                //         .defaultSuccessUrl("/home", true)
                // )  
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
