package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Notification;
import SocialNetwork.SocialNetwork.domain.entities.NotificationType;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.NotificationDTO;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.NotificationPageResponse;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.UserDTO;
import SocialNetwork.SocialNetwork.repositories.NotificationRepository;
import SocialNetwork.SocialNetwork.services.NotificationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(User actor, User recipient, NotificationType type, Long targetId,
            Long relatedId, String message) {
        // Don't create notification if actor is the same as recipient
        if (actor.getId().equals(recipient.getId())) {
            return null;
        }

        // Check if similar notification already exists to avoid duplicates
        Notification existing = notificationRepository.findExistingNotification(actor, recipient, type, targetId);
        if (existing != null) {
            // Update the existing notification timestamp
            existing.setCreatedAt(java.time.LocalDateTime.now());
            existing.setIsRead(false);
            existing.setMessage(message);
            notificationRepository.save(existing);
            return convertToDTO(existing);
        }

        Notification notification = Notification.builder()
                .actor(actor)
                .recipient(recipient)
                .type(type)
                .targetId(targetId)
                .relatedId(relatedId)
                .message(message)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    @Override
    public NotificationPageResponse getNotifications(User user, int page, int limit) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        Page<Notification> notificationPage = notificationRepository.findByRecipientOrderByCreatedAtDesc(user,
                pageable);

        List<NotificationDTO> notificationDTOs = notificationPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return NotificationPageResponse.builder()
                .notifications(notificationDTOs)
                .currentPage(page)
                .totalPages(notificationPage.getTotalPages())
                .totalElements(notificationPage.getTotalElements())
                .hasNext(notificationPage.hasNext())
                .hasPrevious(notificationPage.hasPrevious())
                .build();
    }

    @Override
    public Long getUnreadCount(User user) {
        return notificationRepository.countUnreadByRecipient(user);
    }

    @Override
    @Transactional
    public int markAsRead(User user, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        return notificationRepository.markAsRead(notificationIds, user);
    }

    @Override
    @Transactional
    public int markAllAsRead(User user) {
        return notificationRepository.markAllAsRead(user);
    }

    @Override
    @Transactional
    public boolean deleteNotification(User user, Long notificationId) {
        int deleted = notificationRepository.deleteByIdAndRecipient(notificationId, user);
        return deleted > 0;
    }

    @Override
    public void notifyLikePost(User actor, User postOwner, Long postId) {
        String message = actor.getDisplayname() + " liked your post";
        createNotification(actor, postOwner, NotificationType.LIKE_POST, postId, null, message);
    }

    @Override
    public void notifyLikeComment(User actor, User commentOwner, Long commentId, Long postId) {
        String message = actor.getDisplayname() + " liked your comment";
        createNotification(actor, commentOwner, NotificationType.LIKE_COMMENT, commentId, postId, message);
    }

    @Override
    public void notifyComment(User actor, User postOwner, Long commentId, Long postId) {
        String message = actor.getDisplayname() + " commented on your post";
        createNotification(actor, postOwner, NotificationType.COMMENT, postId, commentId, message);
    }

    @Override
    public void notifyReply(User actor, User commentOwner, Long replyId, Long parentCommentId) {
        String message = actor.getDisplayname() + " replied to your comment";
        createNotification(actor, commentOwner, NotificationType.REPLY, parentCommentId, replyId, message);
    }

    @Override
    public void notifyFollow(User actor, User followedUser) {
        String message = actor.getDisplayname() + " started following you";
        createNotification(actor, followedUser, NotificationType.FOLLOW, null, null, message);
    }

    @Override
    public void notifyMention(User actor, User mentionedUser, Long targetId, NotificationType contextType) {
        String message = actor.getDisplayname() + " mentioned you";
        createNotification(actor, mentionedUser, NotificationType.MENTION, targetId, null, message);
    }

    private NotificationDTO convertToDTO(Notification notification) {
        User actor = notification.getActor();
        UserDTO actorDTO = new UserDTO(
                actor.getId(),
                actor.getUsername(),
                actor.getDisplayname(),
                actor.getAvatar());

        return NotificationDTO.builder()
                .id(notification.getId())
                .actor(actorDTO)
                .type(notification.getType())
                .targetId(notification.getTargetId())
                .relatedId(notification.getRelatedId())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
