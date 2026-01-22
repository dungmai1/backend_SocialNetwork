package SocialNetwork.SocialNetwork.servicesImpl;

import SocialNetwork.SocialNetwork.domain.entities.*;
import SocialNetwork.SocialNetwork.domain.models.ModelsRequest.PostRequest;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.CursorResponse;
import SocialNetwork.SocialNetwork.domain.models.serviceModels.PostDTO;
import SocialNetwork.SocialNetwork.exception.CustomException;
import SocialNetwork.SocialNetwork.repositories.CommentRepository;
import SocialNetwork.SocialNetwork.repositories.PostImageRepository;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostImageRepository postImageRepository;
    private final SavedRepository savedRepository;
    private final RelationshipRepository relationshipRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Autowired
    private CacheManager cacheManager;

    public PostServiceImpl(PostRepository postRepository, UserRepository userRepository,
            SavedRepository savedRepository, RelationshipRepository relationshipRepository, ModelMapper modelMapper,
            PostImageRepository postImageRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.savedRepository = savedRepository;
        this.relationshipRepository = relationshipRepository;
        this.modelMapper = modelMapper;
        this.postImageRepository = postImageRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "posts:username", key = "#user.username"),
            @CacheEvict(value = "posts:savedByUsername", key = "#user.username")
    })
    public PostDTO createPost(String content, List<String> imageUrls, User user) {
        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setStatus(1);
        post.setPostTime(LocalDateTime.now());
        postRepository.save(post);
        for (String imageUrl : imageUrls) {
            PostImage postImage = new PostImage();
            postImage.setPost(post);
            postImage.setImageUrl(imageUrl);
            postImageRepository.save(postImage);
        }
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
    @Caching(evict = {
            @CacheEvict(value = "post:single", key = "#postId"),
            @CacheEvict(value = "posts:username", allEntries = true)
    })
    public boolean deletePost(User user, Long postId) throws CustomException {
        Post PostToRemove = postRepository.findById(postId).orElse(null);
        // Evict comment cache for this post
        safeEvict("comments:postId", postId);
        safeEvict("commentLists:postId", postId);
        safeEvict("post:likeCount", postId);
        safeEvict("post:likeUsers", postId);

        postRepository.delete(PostToRemove);
        PostImage postImage = postImageRepository.findByPost(PostToRemove);
        postImageRepository.delete(postImage);
        Comment comment = commentRepository.findByPost(PostToRemove);
        commentRepository.delete(comment);
        return true;
    }

    @Override
    @Cacheable(value = "post:single", key = "#postId", unless = "#result == null")
    public PostDTO getSinglePost(User user, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return null;
        }
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
            boolean isSaved = savedRepository.existsByUserAndPost(user, post);
            PostDTO.setSaved(isSaved);
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
    @CacheEvict(value = "posts:savedByUsername", key = "#user.username")
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
    @Caching(evict = {
            @CacheEvict(value = "post:single", key = "#postId"),
            @CacheEvict(value = "posts:username", key = "#user.username")
    })
    public boolean updatePost(User user, Long postId, PostRequest postRequest) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        post.setContent(postRequest.getContent());
        postRepository.save(post);
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
    @Cacheable(value = "posts:username", key = "#username", unless = "#result == null || #result.isEmpty()")
    public List<PostDTO> getAllPostsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        List<Post> postList = postRepository.findByUserAndStatusOrderByPostTimeDesc(user, 1);
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
    @CacheEvict(value = "post:single", key = "#postId")
    public void BanPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not found");
        }
        post.setStatus(2);
        postRepository.save(post);
        // Evict username cache for post owner
        safeEvict("posts:username", post.getUser().getUsername());
    }

    @Override
    @CacheEvict(value = "post:single", key = "#postId")
    public void unbanPost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            throw new CustomException("Post not found");
        }
        post.setStatus(1);
        postRepository.save(post);
        // Evict username cache for post owner
        safeEvict("posts:username", post.getUser().getUsername());
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

    @Override
    @Cacheable(value = "posts:savedByUsername", key = "#username", unless = "#result == null || #result.isEmpty()")
    public List<PostDTO> getAllSavedPostsByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        List<Saved> listSaveds = savedRepository.findAllByUser(user);
        List<PostDTO> postDTOs = new ArrayList<>();
        for (Saved saved : listSaveds) {
            Post post = saved.getPost();
            PostDTO postDTO = modelMapper.map(post, PostDTO.class);
            postDTO.setUsername(post.getUser().getUsername());
            postDTO.setAvatar(post.getUser().getAvatar());
            List<String> imageUrls = post.getImages().stream()
                    .map(PostImage::getImageUrl)
                    .collect(Collectors.toList());
            postDTO.setImages(imageUrls);
            postDTOs.add(postDTO);
        }
        return postDTOs;
    }

    // Helper method để evict cache an toàn
    private void safeEvict(String cacheName, Object key) {
        if (key == null)
            return;
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null) {
            cache.evict(key);
        }
    }
}
