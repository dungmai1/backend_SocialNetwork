package SocialNetwork.SocialNetwork.domain.models.bindingModels;

import SocialNetwork.SocialNetwork.domain.entities.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
public class PostCreateBindingModel {
    private String content;
    private String ImageUrl;
}
