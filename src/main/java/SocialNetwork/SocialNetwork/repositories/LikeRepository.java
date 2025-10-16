package SocialNetwork.SocialNetwork.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.TargetType;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    @Query("SELECT l FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType and l.user.id = :userId")
    Like findByUserAndTargetType(@Param("targetId") Long targetId,
                                @Param("targetType") TargetType targetType,
                                @Param("userId") Long userId);
    @Query("SELECT l FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType")
    List<Like> getAllUserLike(@Param("targetId") Long targetId,
                                @Param("targetType") TargetType targetType);
    @Query("SELECT COUNT(l) FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType")
    Long countLikes(@Param("targetId") Long targetId,
                    @Param("targetType") TargetType targetType);
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN TRUE ELSE FALSE END " +
       "FROM Like l WHERE l.targetId = :targetId AND l.targetType = :targetType AND l.user.id = :userId")
    boolean existsByTargetIdAndTargetTypeAndUser(@Param("targetId") Long targetId,
                    @Param("targetType") TargetType targetType,
                    @Param("userId") Long userId);
   @Modifying
   @Transactional
   @Query("DELETE FROM Like l WHERE l.targetType = :targetType AND l.targetId = :targetId")
   void deleteByTargetTypeAndTargetId(TargetType targetType, Long targetId);
}

