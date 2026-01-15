package SocialNetwork.SocialNetwork.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage,Long> {
    PostImage findByPost(Post post);
}
