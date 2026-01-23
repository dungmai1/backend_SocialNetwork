package SocialNetwork.SocialNetwork.domain.models.serviceModels;

import SocialNetwork.SocialNetwork.domain.entities.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private Long id;
    private UserDTO actor;
    private NotificationType type;
    private Long targetId;
    private Long relatedId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
