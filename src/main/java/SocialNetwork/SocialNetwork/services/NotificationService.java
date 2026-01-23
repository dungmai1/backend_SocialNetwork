package SocialNetwork.SocialNetwork.services;

import SocialNetwork.SocialNetwork.domain.entities.NotificationType;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.NotificationDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.NotificationPageResponse;

import java.util.List;

public interface NotificationService {

    /**
     * Create a new notification
     */
    NotificationDTO createNotification(User actor, User recipient, NotificationType type, Long targetId, Long relatedId,
            String message);

    /**
     * Get paginated list of notifications for a user
     */
    NotificationPageResponse getNotifications(User user, int page, int limit);

    /**
     * Get count of unread notifications for a user
     */
    Long getUnreadCount(User user);

    /**
     * Mark specific notifications as read
     */
    int markAsRead(User user, List<Long> notificationIds);

    /**
     * Mark all notifications as read for a user
     */
    int markAllAsRead(User user);

    /**
     * Delete a notification
     */
    boolean deleteNotification(User user, Long notificationId);

    // Convenience methods for creating specific notification types

    /**
     * Create notification when someone likes a post
     */
    void notifyLikePost(User actor, User postOwner, Long postId);

    /**
     * Create notification when someone likes a comment
     */
    void notifyLikeComment(User actor, User commentOwner, Long commentId, Long postId);

    /**
     * Create notification when someone comments on a post
     */
    void notifyComment(User actor, User postOwner, Long commentId, Long postId);

    /**
     * Create notification when someone replies to a comment
     */
    void notifyReply(User actor, User commentOwner, Long replyId, Long parentCommentId);

    /**
     * Create notification when someone follows a user
     */
    void notifyFollow(User actor, User followedUser);

    /**
     * Create notification when someone mentions a user
     */
    void notifyMention(User actor, User mentionedUser, Long targetId, NotificationType contextType);
}
