package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.*;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.PostRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CursorResponse;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.PostRepository;
import SocialNetwork.SocialNetwork.repositories.RelationshipRepository;
import SocialNetwork.SocialNetwork.repositories.SavedRepository;
import SocialNetwork.SocialNetwork.repositories.UserRepository;
import SocialNetwork.SocialNetwork.services.PostService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SavedRepository savedRepository;
    private final RelationshipRepository relationshipRepository;
    private final ModelMapper modelMapper;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
            SavedRepository savedRepository, RelationshipRepository relationshipRepository, ModelMapper modelMapper) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.savedRepository = savedRepository;
        this.relationshipRepository = relationshipRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public PostDTO createPost(PostRequest postRequest, User user) {
        Post post = modelMapper.map(postRequest, Post.class);
        post.setUser(user);
        post.setStatus(1);
        post.setPostTime(LocalDateTime.now());
        postRepository.save(post);
        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
        postDTO.setUsername(user.getUsername());
        postDTO.setAvatar(user.getAvatar());
        return postDTO;
    }

    @Override
    public List<PostDTO> getAllPostsByUser(User user) {
        List<Post> postList = postRepository.findByUser(user);
        List<PostDTO> PostDTOs = new ArrayList<>();
        for (Post post : postList) {
            PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
            PostDTO.setUsername(post.getUser().getUsername());
            PostDTO.setAvatar(post.getUser().getAvatar());
            PostDTOs.add(PostDTO);
        }
        return PostDTOs;
    }

    @Override
    public boolean deletePost(User user, Long PostId) throws CustomException {
        Post PostToRemove = postRepository.findById(PostId).orElse(null);
        postRepository.delete(PostToRemove);
        return true;
    }

    @Override
    public PostDTO getSinglePost(User user, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
        return PostDTO;
    }

    @Override
    public CursorResponse<PostDTO> getAllPosts(LocalDateTime cursor, User user, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> postList;
        if (cursor == null) {
            postList = postRepository.findLatestPosts(pageable);
        } else {
            postList = postRepository.findNextPosts(cursor, pageable);
        }
        List<PostDTO> PostDTOs = new ArrayList<>();
        for (Post post : postList) {
            PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
            PostDTO.setUsername(post.getUser().getUsername());
            PostDTO.setAvatar(post.getUser().getAvatar());
            List<String> imageUrls = post.getImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
            PostDTO.setImages(imageUrls);
            // PostDTO.setNextCursor(PostDTOs.get(PostDTOs.size() - 1).getPostTime());
            PostDTOs.add(PostDTO);
        }
        LocalDateTime nextCursor;
        if (!PostDTOs.isEmpty()) {
            nextCursor = PostDTOs.get(PostDTOs.size() - 1).getPostTime();
        } else {
            nextCursor = null;
        }
        return new CursorResponse<>(PostDTOs, nextCursor);
    }

    @Override
    public boolean savePost(User user, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        Saved checksaved = savedRepository.findByUserAndPost(user, post);
        if (checksaved != null) {
            savedRepository.delete(checksaved);
        } else {
            Saved saved = new Saved();
            saved.setPost(post);
            saved.setUser(user);
            savedRepository.save(saved);
        }
        return true;
    }

    @Override
    public List<PostDTO> GetAllSavedPost(User user) {
        // List<Saved> savedPosts = savedRepository.findAllByUser(user);
        // List<PostDTO> PostDTOs = new ArrayList<>();
        // for (Saved saved : savedPosts) {
        // Post post = saved.getPost();
        // PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
        // PostDTOs.add(PostDTO);
        // }
        // return PostDTOs;
        return null;
    }

    @Override
    public List<PostDTO> getAllPostsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        List<Post> postList = postRepository.findByUserAndStatus(user, 1);
        List<PostDTO> PostDTOs = new ArrayList<>();
        for (Post post : postList) {
            PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
            PostDTO.setUsername(post.getUser().getUsername());
            PostDTO.setAvatar(post.getUser().getAvatar());
            List<String> imageUrls = post.getImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
            PostDTO.setImages(imageUrls);
            PostDTOs.add(PostDTO);
        }
        return PostDTOs;
    }

    @Override
    public List<PostDTO> getAllPostsByImagePath(List<String> imagePaths) {
        // List<Post> postList = postRepository.findByImageUrls(imagePaths);
        // List<PostDTO> PostDTOs = new ArrayList<>();
        // for (Post post : postList) {
        // PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
        // PostDTOs.add(PostDTO);
        // }
        // return PostDTOs;
        return null;
    }

    @Override
    public List<PostDTO> GetAllPostByFollowing(String username) {
        // User user = userRepository.findByUsername(username).orElse(null);
        // if (user == null) {
        // return new ArrayList<>(); // Return an empty list if user is not found
        // }
        // List<Relationship> relationshipList =
        // relationshipRepository.findAllByUserOne(user);
        // List<PostDTO> PostDTOs = new ArrayList<>();
        // for (Relationship relationship : relationshipList) {
        // List<Post> postList =
        // postRepository.findAllByUserAndStatus(relationship.getUserTwo(),1);
        // for (Post post : postList) {
        // PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
        // PostDTOs.add(PostDTO);
        // }
        // }
        // return PostDTOs;
        return null;
    }

    @Override
    public void BanPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not found");
        }
        post.setStatus(2);
        postRepository.save(post);
    }

    @Override
    public void unbanPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not found");
        }
        post.setStatus(1);
        postRepository.save(post);
    }

    @Override
    public List<PostDTO> getAllPostBan(User user) {
        // List<Post> postList = postRepository.findLatestPosts();
        // List<PostDTO> PostDTOs = new ArrayList<>();
        // for (Post post : postList) {
        // PostDTO PostDTO = modelMapper.map(post, PostDTO.class);
        // PostDTOs.add(PostDTO);
        // }
        // return PostDTOs;
        return null;
    }
}
