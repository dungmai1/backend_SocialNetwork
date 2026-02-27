package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.MarkReadRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.NotificationPageResponse;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.NotificationService;
import SocialNetwork.SocialNetwork.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    /**
     * Get paginated list of notifications
     * GET /notifications?page=1&limit=20
     */
    @GetMapping
    public ResponseEntity<?> getNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            NotificationPageResponse response = notificationService.getNotifications(user, page, limit);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get count of unread notifications
     * GET /notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            Long unreadCount = notificationService.getUnreadCount(user);
            Map<String, Long> response = new HashMap<>();
            response.put("unreadCount", unreadCount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Mark specific notifications as read
     * PUT /notifications/mark-read
     */
    @PutMapping("/mark-read")
    public ResponseEntity<?> markAsRead(
            @RequestBody MarkReadRequest request,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            int updated = notificationService.markAsRead(user, request.getNotificationIds());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("updatedCount", updated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Mark all notifications as read
     * PUT /notifications/mark-all-read
     */
    @PutMapping("/mark-all-read")
    public ResponseEntity<?> markAllAsRead(
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            int updated = notificationService.markAllAsRead(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("updatedCount", updated);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a notification
     * DELETE /notifications/{notificationId}
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<?> deleteNotification(
            @PathVariable Long notificationId,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            boolean deleted = notificationService.deleteNotification(user, notificationId);
            if (deleted) {
                return new ResponseEntity<>(new ApiResponse(true, "Notification deleted successfully"), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        new ApiResponse(false, "Notification not found or you don't have permission to delete it"),
                        HttpStatus.NOT_FOUND);
            }
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
