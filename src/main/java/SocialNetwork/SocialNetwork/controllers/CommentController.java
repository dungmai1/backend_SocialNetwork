package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.CommentRequest;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.EditCommentRequest;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.RepCommentRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CommentDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.CommentService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = { "http://localhost:3000", "http://localhost:3001" })
@RestController
@RequestMapping("/comments")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createComment(@RequestBody CommentRequest CommentRequest,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            CommentDTO commentDTO = commentService.addComment(CommentRequest, user);
            return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/count")
    public ResponseEntity<?> CountComment(Long postId) {
        try {
            Integer countComment = commentService.CountAllCommentsForPost(postId);
            return new ResponseEntity<>(countComment, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteComment(@CookieValue(value = "accessToken", required = false) String jwt,
            Long postId, Long commentId) {
        try {
            User user = userService.findUserByJwt(jwt);
            commentService.deleteComment(user, postId, commentId);
            return new ResponseEntity<>(new ApiResponse(true, "Delete Comment Success"), HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/posts")
    public List<CommentDTO> getAllCommentForPost(Long postId) {
        List<CommentDTO> commentList = commentService.getAllCommentForPost(postId);
        return commentList;
    }

    @PostMapping("/replies/create")
    public ResponseEntity<?> createReply(@RequestBody RepCommentRequest repCommentRequest,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            CommentDTO commentDTO = commentService.addRepComment(repCommentRequest, user);
            return new ResponseEntity<>(commentDTO, HttpStatus.CREATED);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/replies/count")
    public ResponseEntity<?> CountReply(Long commentId) {
        try {
            Integer countReply = commentService.CountAllRepComment(commentId);
            return new ResponseEntity<>(countReply, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/replies")
    public List<CommentDTO> getAllReplyForComment(Long commentId) {
        List<CommentDTO> replyList = commentService.getAllRepComment(commentId);
        return replyList;
    }

    @PutMapping("/edit")
    public ResponseEntity<?> editComment(@RequestBody EditCommentRequest editCommentRequest,
            @CookieValue(value = "accessToken", required = false) String jwt) {
        try {
            User user = userService.findUserByJwt(jwt);
            CommentDTO commentDTO = commentService.editComment(editCommentRequest, user);
            return new ResponseEntity<>(commentDTO, HttpStatus.OK);
        } catch (CustomException e) {
            return new ResponseEntity<>(new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
