package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByUser(User user);
    List<Post> findByUserAndStatusOrderByPostTimeDesc(User user,Integer status);
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.status= 1 ORDER BY p.postTime DESC")
    List<Post> findLatestPosts(Pageable pageable);
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.status= 1 AND p.postTime < :cursorTime ORDER BY p.postTime DESC")
    List<Post> findNextPosts(@Param("cursorTime") LocalDateTime cursorTime, Pageable pageable);
}
