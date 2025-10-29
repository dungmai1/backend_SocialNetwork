package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Relationship;
import SocialNetwork.SocialNetwork.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship,Long> {
    boolean existsByUserOneAndUserTwo(Long userOne, Long userTwo);
    boolean existsByUserTwoAndUserOne(Long userTwo, Long userOne);
    @Query("SELECT r.userTwo FROM Relationship r WHERE r.userOne = :user")
    List<Long> findAllByUserOne(User user);
    @Query("SELECT r.userOne FROM Relationship r WHERE r.userTwo = :user")
    List<Long> findAllByUserTwo(User user);
    @Query("SELECT COUNT(r) FROM Relationship r WHERE r.userTwo = :userId")
    Long countFollower(Long userId);
    @Query("SELECT COUNT(r) FROM Relationship r WHERE r.userOne = :userId")
    Long countFollowing(Long userId);
    Relationship findByUserOneAndUserTwo(Long userOne, Long userTwo);
}
