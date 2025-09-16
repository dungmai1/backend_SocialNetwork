package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByPhone(String Phone);
    Optional<User> findByUsname(String username);
    @Query("SELECT u FROM User u WHERE u.usname LIKE %:usname%")
    List<User> findByUsnameContaining(@Param("usname") String usname);
}
