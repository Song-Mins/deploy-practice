package com.example.deploy.domain.post.service;

import com.example.deploy.domain.Image.service.ImageFileService;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.model.entity.PostMeta;
import com.example.deploy.domain.post.model.request.PostRequest;
import com.example.deploy.domain.post.model.request.PostSearchRequest;
import com.example.deploy.domain.post.model.response.PostResponse;
import com.example.deploy.domain.post.repository.PostRepository;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.model.response.PagedResponse;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@RequiredArgsConstructor
public abstract class AbstractPostService {

    protected final PostRepository postRepository;
    protected final UserRepository userRepository;
    protected final ImageFileService imageFileService;

    @Transactional
    public PostResponse createPost(
            Long userId, PostRequest postRequest, List<MultipartFile> imageFiles) {
        User user = userRepository.getUserById(userId);
        Post post = createPostByCategoryType(postRequest, user);
        PostMeta postMeta = PostMeta.builder().post(post).viewCount(0L).commentCount(0L).build();

        post.setPostMeta(postMeta);
        postRepository.save(post);

        List<String> imageUrls = saveImages(imageFiles, post);
        String userImageUrl = post.getUser().getProfileImageUrl();

        return PostResponse.responseWithoutContentPreview(
                post, userImageUrl, imageUrls, null, null);
    }

    @Transactional
    public PostResponse updatePost(
            Long userId, Long postId, PostRequest postRequest, List<MultipartFile> imageFiles) {
        Post post = postRepository.getPostByIdAndDeletedFalse(postId);
        post.updateBy(userId, postRequest);

        List<String> imageUrls =
                updateImages(
                        imageFiles != null ? imageFiles : Collections.emptyList(),
                        post,
                        postRequest.deletedAttachedFiles());

        String userImageUrl = post.getUser().getProfileImageUrl();

        return PostResponse.responseWithoutContentPreview(
                post, userImageUrl, imageUrls, null, null);
    }

    @Transactional
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.getPostByIdAndDeletedFalse(postId);
        post.deleteBy(userId);
        postRepository.save(post);
    }

    @Transactional
    public abstract PostResponse getPost(
            Long userId, Long postId, PostSearchRequest postSearchRequest);

    @Transactional(readOnly = true)
    public abstract PagedResponse<PostResponse> getAllPosts(Long userId, Pageable pageable);

    @Transactional(readOnly = true)
    public abstract PagedResponse<PostResponse> searchPosts(
            Long userId, PostSearchRequest request, Pageable pageable);

    protected PagedResponse<PostResponse> mapPostsToPagedResponse(Page<Post> postsPage) {
        List<PostResponse> communities =
                postsPage.stream()
                        .map(
                                post ->
                                        PostResponse.responseWithContentPreview(
                                                post, post.getUser().getProfileImageUrl()))
                        .collect(Collectors.toList());

        return new PagedResponse<>(
                communities, postsPage.getTotalElements(), postsPage.getTotalPages());
    }

    protected abstract List<String> saveImages(List<MultipartFile> imageFiles, Post post);

    protected abstract List<String> updateImages(
            List<MultipartFile> imageFiles, Post post, List<String> deletedImageFiles);

    protected abstract Post createPostByCategoryType(PostRequest postRequest, User user);
}
