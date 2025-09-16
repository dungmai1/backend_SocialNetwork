package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Relationship;
import SocialNetwork.SocialNetwork.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship,Integer> {
    Relationship findByUserOneAndUserTwo(User user1,User user2);
    List<Relationship> findAllByUserOne(User user);
    List<Relationship> findAllByUserTwo(User user);

//    @Query(value = "" +
//            "SELECT r FROM Relationship AS r " +
//            "WHERE ((r.userOne.id = :id1 AND r.userTwo.id = :id2) " +
//            "OR ( r.userTwo.id = :id1 AND r.userOne.id = :id2)) ")
//    Relationship findRelationshipByUserOneIdAndUserTwoId(@Param(value = "id1") Integer userOneId,
//                                                         @Param(value = "id2") Integer userTwoId);
//
//    @Query(value = "" +
//            "SELECT r FROM Relationship AS r " +
//            "WHERE ((r.userOne.id = :id1 AND r.userTwo.id = :id2) " +
//            "OR ( r.userTwo.id = :id1 AND r.userOne.id = :id2)) " +
//            "AND r.status = :status")
//    Relationship findRelationshipWithFriendWithStatus(@Param(value = "id1") Integer userOneId,
//                                                      @Param(value = "id2") Integer userTwoId,
//                                                      @Param(value = "status") int status);
//    @Query(value = "" +
//            "SELECT r FROM Relationship AS r " +
//            "WHERE (r.userOne.id = :id OR r.userTwo.id = :id) " +
//            "AND r.status  NOT IN (0 , 2)")
//    List<Relationship> findAllNotCandidatesForFriends(@Param(value = "id") Integer id);




}
