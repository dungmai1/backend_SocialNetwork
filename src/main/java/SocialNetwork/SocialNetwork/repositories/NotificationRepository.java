package SocialNetwork.SocialNetwork.repositories;

import SocialNetwork.SocialNetwork.domain.entities.Notification;
import SocialNetwork.SocialNetwork.domain.entities.NotificationType;
import SocialNetwork.SocialNetwork.domain.entities.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientOrderByCreatedAtDesc(User recipient, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient = :recipient AND n.isRead = false")
    Long countUnreadByRecipient(@Param("recipient") User recipient);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids AND n.recipient = :recipient")
    int markAsRead(@Param("ids") List<Long> ids, @Param("recipient") User recipient);

    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient = :recipient AND n.isRead = false")
    int markAllAsRead(@Param("recipient") User recipient);

    @Query("SELECT n FROM Notification n WHERE n.recipient = :recipient AND n.id = :id")
    Notification findByIdAndRecipient(@Param("id") Long id, @Param("recipient") User recipient);

    @Modifying
    @Transactional
    @Query("DELETE FROM Notification n WHERE n.id = :id AND n.recipient = :recipient")
    int deleteByIdAndRecipient(@Param("id") Long id, @Param("recipient") User recipient);

    @Query("SELECT n FROM Notification n WHERE n.actor = :actor AND n.recipient = :recipient AND n.type = :type AND n.targetId = :targetId")
    Notification findExistingNotification(
            @Param("actor") User actor,
            @Param("recipient") User recipient,
            @Param("type") NotificationType type,
            @Param("targetId") Long targetId);
}
