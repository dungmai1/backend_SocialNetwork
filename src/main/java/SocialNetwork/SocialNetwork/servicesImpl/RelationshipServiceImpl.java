package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Relationship;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserProfileDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.RelationshipRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.NotificationService;
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
    private NotificationService notificationService;

    public RelationshipServiceImpl(UserRepository userRepository, RelationshipRepository relationshipRepository,
            NotificationService notificationService) {
        this.userRepository = userRepository;
        this.relationshipRepository = relationshipRepository;
        this.notificationService = notificationService;
    }

    @Override
    public boolean addFollow(User user, String username) throws CustomException {
        User targetUser = userRepository.findByUsername(username).orElse(null);
        Relationship checkFriendRelationship = relationshipRepository.findByUserOneAndUserTwo(user.getId(),
                targetUser.getId());
        if (checkFriendRelationship != null) {
            relationshipRepository.delete(checkFriendRelationship);
        } else {
            Relationship relationship = new Relationship();
            relationship.setUserOne(user.getId());
            relationship.setUserTwo(targetUser.getId());
            relationship.setStatus(1);
            relationshipRepository.save(relationship);
            // Send notification to followed user
            notificationService.notifyFollow(user, targetUser);
        }
        return true;
    }

    @Override
    public List<UserProfileDTO> getFollower(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException("User not found");
        }

        List<Long> followerIds = relationshipRepository.findAllByUserTwo(user.getId());
        List<UserProfileDTO> followers = new ArrayList<>();
        for (Long followerId : followerIds) {
            User follower = userRepository.findById(followerId).orElse(null);
            if (follower != null) {
                // Kiểm tra xem người dùng hiện tại có theo dõi lại người này không
                boolean isFollowing = relationshipRepository.existsByUserOneAndUserTwo(user.getId(), followerId);

                UserProfileDTO dto = new UserProfileDTO();
                dto.setId(follower.getId());
                dto.setUsername(follower.getUsername());
                dto.setAvatar(follower.getAvatar());
                dto.setDescription(follower.getDescription());

                UserProfileDTO.RelationshipInfo relationshipInfo = new UserProfileDTO.RelationshipInfo();
                relationshipInfo.setSelf(false);
                relationshipInfo.setFollowing(isFollowing);
                relationshipInfo.setFollower(true);
                relationshipInfo.setFollowerCount(relationshipRepository.countFollower(followerId));
                relationshipInfo.setFollowingCount(relationshipRepository.countFollowing(followerId));

                dto.setRelationship(relationshipInfo);
                followers.add(dto);
            }
        }
        return followers;
    }

    @Override
    public List<UserProfileDTO> getFollowing(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            throw new CustomException("User not found");
        }
        List<Long> followingIds = relationshipRepository.findAllByUserOne(user.getId());
        List<UserProfileDTO> following = new ArrayList<>();
        for (Long userId : followingIds) {
            User followedUser = userRepository.findById(userId).orElse(null);
            if (followedUser != null) {
                boolean isFollower = relationshipRepository.existsByUserOneAndUserTwo(userId, user.getId());
                UserProfileDTO dto = new UserProfileDTO();
                dto.setId(followedUser.getId());
                dto.setUsername(followedUser.getUsername());
                dto.setAvatar(followedUser.getAvatar());
                dto.setDescription(followedUser.getDescription());

                UserProfileDTO.RelationshipInfo relationshipInfo = new UserProfileDTO.RelationshipInfo();
                relationshipInfo.setSelf(false);
                relationshipInfo.setFollowing(true);
                relationshipInfo.setFollower(isFollower);
                relationshipInfo.setFollowerCount(relationshipRepository.countFollower(userId));
                relationshipInfo.setFollowingCount(relationshipRepository.countFollowing(userId));

                dto.setRelationship(relationshipInfo);
                following.add(dto);
            }
        }
        return following;
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
