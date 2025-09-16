package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Like;
import SocialNetwork.SocialNetwork.domain.entities.Relationship;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserServiceModel;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.RelationshipRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.RelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RelationshipServiceImpl implements RelationshipService {
    private UserRepository userRepository;
    private RelationshipRepository relationshipRepository;

    public RelationshipServiceImpl(UserRepository userRepository, RelationshipRepository relationshipRepository) {
        this.userRepository = userRepository;
        this.relationshipRepository = relationshipRepository;
    }

    @Override
    public boolean CreateRequestAddingFriend(User user, String username) throws CustomException{
        User friendCandidate = userRepository.findByUsname(username).orElse(null);
        if( friendCandidate == null || friendCandidate == user ) {
            throw new CustomException("User not found");
        }
        Relationship checkFriendRelationship = relationshipRepository.findByUserOneAndUserTwo(user,friendCandidate);
        if(checkFriendRelationship != null) {
            relationshipRepository.delete(checkFriendRelationship);
            throw new CustomException("Delete relationship");
        }else{
            Relationship relationship = new Relationship();
            relationship.setUserOne(user);
            relationship.setUserTwo(friendCandidate);
            relationship.setStatus(1);
            relationshipRepository.save(relationship);
        }
        return true;
    }

    @Override
    public List<User> getFollower(String username) {
        User user = userRepository.findByUsname(username).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        List<Relationship> relationshipList = relationshipRepository.findAllByUserTwo(user);
        List<User> users = new ArrayList<>();
        for (Relationship relationship : relationshipList) {
            users.add(relationship.getUserOne());
        }
        return users;
    }

    @Override
    public List<User> getFollowing(String username) {
        User user = userRepository.findByUsname(username).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }
        List<Relationship> relationshipList = relationshipRepository.findAllByUserOne(user);
        List<User> users = new ArrayList<>();
        for (Relationship relationship : relationshipList) {
            users.add(relationship.getUserTwo());
        }
        return users;
    }

    @Override
    public boolean checkFollow(User user, String username) {
        User checkuser = userRepository.findByUsname(username).orElse(null);
        if(checkuser == user){
            throw new CustomException("This user is yours");
        }
        Relationship relationship = relationshipRepository.findByUserOneAndUserTwo(user,checkuser);
        if(relationship != null) {
            return false;
        }
        return true;
    }
//    @Override
//    public List<User> getAllFriendOfUser(String phone) {
//        User user = userRepository.findByPhone(phone).orElse(null);
//        List<Relationship> relationshipList = relationshipRepository.findAllNotCandidatesForFriends(user.getId());
//        List<User> users = new ArrayList<>();
//        for (Relationship relationship : relationshipList) {
//            if (!relationship.getUserOne().getId().equals(user.getId())) {
//                users.add(relationship.getUserOne());
//            } else {
//                users.add(relationship.getUserTwo());
//            }
//        }
//        return users;
//    }
}
