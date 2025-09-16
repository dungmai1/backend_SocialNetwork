package SocialNetwork.SocialNetwork.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "Posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String content;
    @ManyToOne
    @JsonIgnore
    private User user;
    private LocalDateTime PostTime;
    private int status;
    private String ImageUrl;
    @OneToMany
    @JsonIgnore
    private List<Like> likeList;
    @OneToMany
    @JsonIgnore
    private List<Comment> commentList;
}
