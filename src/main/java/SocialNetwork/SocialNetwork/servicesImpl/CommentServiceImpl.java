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
    public boolean addComment(CommentRequest CommentRequest, User user) {
        Post post = postRepository.findById(CommentRequest.getPostId()).orElse(null);
        if(post == null) {
            throw new CustomException("PostId not found");
        }
        CommentDTO CommentDTO = new CommentDTO();
        CommentDTO.setCommentTime(LocalDateTime.now());
        CommentDTO.setUser(user);
        CommentDTO.setPost(post);
        CommentDTO.setImageUrl(CommentRequest.getImageUrl());
        CommentDTO.setContent_cmt(CommentRequest.getContent_cmt());
        Comment comment = this.modelMapper.map(CommentDTO, Comment.class);
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
    public List<Comment> getAllCommentForPost(Integer postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if(post == null) {
            throw new CustomException("userId or PostId not found");
        }
        List<Comment> commentList = commentRepository.findAllByPost(post);
        return commentList;
    }
}
