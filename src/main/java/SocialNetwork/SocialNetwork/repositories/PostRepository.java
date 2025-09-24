package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findByUser(User user);
    List<Post> findByUserAndStatus(User user,Integer status);
    List<Post> findAllByStatus(Integer status);
    // @Query("SELECT p FROM Post p WHERE p.ImageUrl IN :imagePaths")
    // List<Post> findByImageUrls(@Param("imagePaths") List<String> imagePaths);
}
