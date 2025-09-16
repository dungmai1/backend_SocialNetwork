package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {
    List<Comment> findAllByPost(Post post);
    Comment findByUserAndPostAndAndId(User user, Post post, Integer CommentId);
}
