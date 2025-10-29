package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private String avatar;
    private String bio;
    private RelationshipInfo relationship;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RelationshipInfo {
        private boolean isSelf;
        private boolean isFollowing;
        private boolean isFollower;
        private long followerCount;
        private long followingCount;
    }
}
