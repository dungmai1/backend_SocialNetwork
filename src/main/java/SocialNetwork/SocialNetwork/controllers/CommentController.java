package SocialNetwork.SocialNetwork.controllers;

import SocialNetwork.SocialNetwork.common.ApiResponse;
import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.bindingModels.CommentCreateBindingModel;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.services.CommentService;
import SocialNetwork.SocialNetwork.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserService userService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createComment(@RequestBody CommentCreateBindingModel commentCreateBindingModel,
                                                     @RequestHeader("Authorization") String jwt){
        try{
            User user = userService.findUserByJwt(jwt);
            commentService.addComment(commentCreateBindingModel,user);
            return new ResponseEntity<>(new ApiResponse(true,"Add Comment Success"), HttpStatus.CREATED);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false,e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/CountAllCommentForPost")
    public ResponseEntity CountComment(Integer PostId) {
        try {
            Integer countComment = commentService.CountAllCommentsForPost(PostId);
            return ResponseEntity.ok(countComment);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteComment(@RequestHeader("Authorization") String jwt,
                                                     Integer PostId, Integer CommentId) {
        try{
            User user = userService.findUserByJwt(jwt);
            commentService.deleteComment(user,PostId,CommentId);
            return new ResponseEntity<>(new ApiResponse(true,"Delete Comment Success"), HttpStatus.OK);
        }catch (CustomException e){
            return new ResponseEntity<>(new ApiResponse(false,e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
    @GetMapping("/getAllCommentForPost")
    public List<Comment> getAllCommentForPost(Integer PostId){
        List<Comment> commentList = commentService.getAllCommentForPost(PostId);
        return commentList;
    }
    @GetMapping("/countAllComment")
    public ResponseEntity countAllComment(){
        try {
            Integer countComment = commentService.countAllComment();
            return ResponseEntity.ok(countComment);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
