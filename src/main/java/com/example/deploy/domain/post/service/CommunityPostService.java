package com.example.deploy.domain.post.service;



import com.example.deploy.domain.Image.entity.enums.ContentType;
import com.example.deploy.domain.Image.service.ImageFileService;
import com.example.deploy.domain.post.event.PostReadEvent;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.model.entity.enums.CategoryType;
import com.example.deploy.domain.post.model.request.PostRequest;
import com.example.deploy.domain.post.model.request.PostSearchRequest;
import com.example.deploy.domain.post.model.response.PostResponse;
import com.example.deploy.domain.post.repository.PostRepository;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.model.response.PagedResponse;
import com.example.deploy.global.type.S3PathPrefixType;
import java.util.Collections;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CommunityPostService extends AbstractPostService {

    private final ApplicationEventPublisher eventPublisher;

    public CommunityPostService(
            PostRepository postRepository,
            UserRepository userRepository,
            ImageFileService imageFileService,
            ApplicationEventPublisher eventPublisher) {
        super(postRepository, userRepository, imageFileService);
        this.eventPublisher = eventPublisher;
    }

    @Override
    public PostResponse getPost(Long userId, Long postId, PostSearchRequest postSearchRequest) {
        User user = userRepository.getUserById(userId);
        Post post = postRepository.getPostByIdAndDeletedFalse(postId);
        String userImageUrl = post.getUser().getProfileImageUrl();
        Long prevPosId =
                postRepository.findPreviousPost(
                        postId,
                        user.getRole(),
                        CategoryType.COMMUNITY,
                        postSearchRequest.communityType(),
                        null,
                        postSearchRequest.keyword());
        Long nextPostId =
                postRepository.findNextPost(
                        postId,
                        user.getRole(),
                        CategoryType.COMMUNITY,
                        postSearchRequest.communityType(),
                        null,
                        postSearchRequest.keyword());

        // 게시물의 모든 이미지 url 리스트를 반환
        List<String> imageUrls = imageFileService.findImageUrls(post.getId(), ContentType.POST);

        // 조회 이벤트 발생 시, 이미 조회된 Post 객체를 전달
        eventPublisher.publishEvent(new PostReadEvent(post));

        return PostResponse.responseWithoutContentPreview(
                post, userImageUrl, imageUrls, prevPosId, nextPostId);
    }

    @Override
    public PagedResponse<PostResponse> getAllPosts(Long userId, Pageable pageable) {
        User user = userRepository.getUserById(userId);
        Page<Post> postsPage = postRepository.findCommunityPostsByRole(user.getRole(), pageable);
        return mapPostsToPagedResponse(postsPage);
    }

    @Override
    public PagedResponse<PostResponse> searchPosts(
            Long userId, PostSearchRequest request, Pageable pageable) {
        User user = userRepository.getUserById(userId);

        Page<Post> posts =
                postRepository.searchCommunityPost(
                        user.getRole(), request.keyword(), request.communityType(), pageable);
        return mapPostsToPagedResponse(posts);
    }

    @Override
    protected List<String> saveImages(List<MultipartFile> imageFiles, Post post) {
        imageFileService.saveImageFiles(
                imageFiles, ContentType.POST, post.getId(), S3PathPrefixType.S3_COMMUNITY_PATH);
        return imageFileService.findImageUrls(post.getId(), ContentType.POST);
    }

    @Override
    protected List<String> updateImages(
            List<MultipartFile> imageFiles, Post post, List<String> deletedImageFiles) {
        if (deletedImageFiles == null) {
            deletedImageFiles = Collections.emptyList();
        }
        imageFileService.saveImageFiles(
                imageFiles, ContentType.POST, post.getId(), S3PathPrefixType.S3_COMMUNITY_PATH);
        imageFileService.deleteImageFiles(deletedImageFiles, S3PathPrefixType.S3_COMMUNITY_PATH);
        return imageFileService.findImageUrls(post.getId(), ContentType.POST);
    }

    @Override
    protected Post createPostByCategoryType(PostRequest postRequest, User user) {
        return Post.createCommunityPost(postRequest, user);
    }
}
