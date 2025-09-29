package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByPost(Post post);
    Comment findByUserAndPostAndId(User user, Post post, Long commentId);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post = :post")
    int countByPost(@Param("post") Post post);
}
