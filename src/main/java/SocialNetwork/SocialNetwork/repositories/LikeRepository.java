package SocialNetwork.SocialNetwork.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface LikeRepository extends JpaRepository<Like,Integer> {
    Like findByUserAndPost(User user, Post post);
    List<Like> findAllLikesByPost(Post post);
    @Query("SELECT COUNT(l) FROM Like l WHERE l.post = :post")
    Long countByPost(@Param("post") Post post);
}
// public class LikeRepository {
//     private final RedisTemplate<String, Object> redisTemplate;
//     private final ValueOperations<String, Object> valueOps;
//     private final SetOperations<String, Object> setOps;

//     public LikeRepository(RedisTemplate<String, Object> redisTemplate) {
//         this.redisTemplate = redisTemplate;
//         this.valueOps = redisTemplate.opsForValue();
//         this.setOps = redisTemplate.opsForSet();
//     }

//     private String getLikeCountKey(Long postId) {
//         return "post:" + postId + ":like_count";
//     }

//     private String getLikeSetKey(Long postId) {
//         return "post:" + postId + ":likes";
//     }

//     // tăng like count
//     public Long incrementLikeCount(Long postId) {
//         return valueOps.increment(getLikeCountKey(postId));
//     }

//     // giảm like count
//     public Long decrementLikeCount(Long postId) {
//         return valueOps.decrement(getLikeCountKey(postId));
//     }
//     public Long getLikeCount(Long postId) {
//         Object val = valueOps.get(getLikeCountKey(postId));
//         return val == null ? 0L : Long.parseLong(val.toString());
//     }
// }
