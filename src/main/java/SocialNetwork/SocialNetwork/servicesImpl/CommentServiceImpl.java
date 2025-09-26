package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.Comment;
import SocialNetwork.SocialNetwork.domain.entities.Post;
import SocialNetwork.SocialNetwork.domain.entities.User;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.CommentRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CommentDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.CommentRepository;
import SocialNetwork.SocialNetwork.repositories.PostRepository;
import SocialNetwork.SocialNetwork.services.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private CommentRepository commentRepository;
    private PostRepository postRepository;
    private ModelMapper modelMapper;

    public CommentServiceImpl(CommentRepository commentRepository, PostRepository postRepository, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean addComment(CommentRequest commentRequest, User user) {
        Post post = postRepository.findById(commentRequest.getPostId()).orElse(null);
        if(post == null) {
            throw new CustomException("PostId not found");
        }
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContentCmt());
        comment.setPost(post);
        comment.setUser(user);
        comment.setImageUrl(commentRequest.getImageUrl());
        comment.setCommentTime(LocalDateTime.now());
        commentRepository.save(comment);
        return true;
    }

    @Override
    public int CountAllCommentsForPost(Integer postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null){
            throw new CustomException("PostId not found");
        }
        return this.commentRepository.countByPost(post);
    }

    @Override
    public void deleteComment(User user, Integer postId, Integer commentId) {
        Post post = postRepository.findById(postId).orElse(null);
        if( post == null) {
            throw new CustomException("userId or PostId not found");
        }
        Comment comment = commentRepository.findByUserAndPostAndAndId(user,post,commentId);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDTO> getAllCommentForPost(Integer postId) {
        Post post = postRepository.findById(postId).get();
        if(post == null) {
            throw new CustomException("userId or PostId not found");
        }
        List<Comment> commentList = commentRepository.findAllByPost(post);
        return commentList.stream()
            .map(c -> new CommentDTO(
                c.getId(),
                c.getContent(),
                c.getPost().getId(),
                c.getUser().getDisplayname(),
                c.getUser().getAvatar(),
                c.getImageUrl(),
                c.getCommentTime()
            ))
            .collect(Collectors.toList());
    }
}
