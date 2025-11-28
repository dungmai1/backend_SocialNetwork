package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Relationship;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.RelationshipRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.RelationshipService;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Service
public class RelationshipServiceImpl implements RelationshipService {
    private UserRepository userRepository;
    private RelationshipRepository relationshipRepository;

    public RelationshipServiceImpl(UserRepository userRepository, RelationshipRepository relationshipRepository) {
        this.userRepository = userRepository;
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public boolean addFollow(User user, String username) throws CustomException {
        User targetUser = userRepository.findByUsername(username).orElse(null);
        Relationship checkFriendRelationship = relationshipRepository.findByUserOneAndUserTwo(user.getId(),
                targetUser.getId());
        if (checkFriendRelationship != null) {
            relationshipRepository.delete(checkFriendRelationship);
            throw new CustomException("Delete relationship");
        } else {
            Relationship relationship = new Relationship();
            relationship.setUserOne(user.getId());
            relationship.setUserTwo(targetUser.getId());
            relationship.setStatus(1);
            relationshipRepository.save(relationship);
        }
        return true;
    }

    @Override
    public List<User> getFollower(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException("User not found");
        }
        List<User> followerIds = relationshipRepository.findAllByUserTwo(user);
        return null;
    }

    @Override
    public List<User> getFollowing(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException("User not found");
        }
        // List<User> followingIds = relationshipRepository.findAllByUserOne(user);
        return null;
    }

    @Override
    public boolean checkFollow(User user, String username) {
        // User checkuser = userRepository.findByUsername(username).orElse(null);
        // if(checkuser == user){
        // return true;
        // }
        // Relationship relationship =
        // relationshipRepository.findByUserOneAndUserTwo(user.getId(),checkuser.getId());
        // if(relationship != null) {
        // return false;
        // }
        return true;
    }

    // @Override
    // public List<User> getAllFriendOfUser(String phone) {
    // User user = userRepository.findByPhone(phone).orElse(null);
    // List<Relationship> relationshipList =
    // relationshipRepository.findAllNotCandidatesForFriends(user.getId());
    // List<User> users = new ArrayList<>();
    // for (Relationship relationship : relationshipList) {
    // if (!relationship.getUserOne().getId().equals(user.getId())) {
    // users.add(relationship.getUserOne());
    // } else {
    // users.add(relationship.getUserTwo());
    // }
    // }
    // return users;
    // }
    @Override
    public Long countFollower(Long userId) {
        return relationshipRepository.countFollower(userId);
    }

    @Override
    public Long countFollowing(Long userId) {
        return relationshipRepository.countFollowing(userId);
    }

    @Override
    public List<UserDTO> recommendUser(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException("User not found");
        }
        // Get IDs of users already being followed
        List<Long> followingIds = relationshipRepository.findAllByUserOne(user.getId());
        if (followingIds == null) {
            followingIds = new ArrayList<>();
        }

        // Get all users and filter out self and already followed, mapping to DTO
        List<UserDTO> candidates = new ArrayList<>();
        for (User u : userRepository.findAll()) {
            if (!u.getId().equals(user.getId()) && !followingIds.contains(u.getId())) {
                UserDTO dto = new UserDTO(u.getId(), u.getUsername(), u.getDisplayname(), u.getAvatar());
                candidates.add(dto);
            }
        }

        // Shuffle and return up to 3 random users
        Collections.shuffle(candidates);
        List<UserDTO> result = new ArrayList<>();
        int limit = Math.min(3, candidates.size());
        for (int i = 0; i < limit; i++) {
            result.add(candidates.get(i));
        }
        return result;
    }
}
