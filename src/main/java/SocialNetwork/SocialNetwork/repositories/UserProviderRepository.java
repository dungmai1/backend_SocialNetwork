package SocialNetwork.SocialNetwork.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import SocialNetwork.SocialNetwork.domain.entities.UserProvider;

@Repository
public interface UserProviderRepository extends JpaRepository<UserProvider, Long>{    
} 
