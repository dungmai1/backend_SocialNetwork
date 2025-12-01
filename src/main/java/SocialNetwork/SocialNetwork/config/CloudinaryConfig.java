package SocialNetwork.SocialNetwork.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {
    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dgjz2u0ps",
                "api_key", "338956965294212",
                "api_secret", "ihrKswo6Rdwk_JR-CLpovGDVvnI",
                "secure", true));
    }
}
