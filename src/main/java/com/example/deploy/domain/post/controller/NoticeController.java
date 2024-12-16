package com.example.deploy.domain.post.controller;


import com.example.deploy.domain.post.model.request.PostRequest;
import com.example.deploy.domain.post.model.request.PostSearchRequest;
import com.example.deploy.domain.post.model.response.PostResponse;
import com.example.deploy.domain.post.service.NoticePostService;
import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.global.api.API;
import com.example.deploy.global.model.response.PagedResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/notices")
public class NoticeController {

    private final NoticePostService noticePostService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> createNoticePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("data") PostRequest postRequest,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles) {
        PostResponse postResponse =
                noticePostService.createPost(
                        customUserDetails.getUserId(), postRequest, imageFiles);
        return API.OK(postResponse);
    }

    // 단건조회
    @GetMapping("/{postId}")
    public ResponseEntity<?> getNoticePost(
            @PathVariable Long postId, @ModelAttribute PostSearchRequest postRequest) {
        PostResponse postResponse = noticePostService.getPost(null, postId, postRequest);
        return API.OK(postResponse);
    }

    // 수정
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PatchMapping("/{postId}")
    public ResponseEntity<?> updateNoticePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @RequestPart("data") PostRequest postRequest,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles) {
        PostResponse postResponse =
                noticePostService.updatePost(
                        customUserDetails.getUserId(), postId, postRequest, imageFiles);
        return API.OK(postResponse);
    }

    // 삭제(물리적 삭제 x)
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteNoticePost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId) {
        noticePostService.deletePost(customUserDetails.getUserId(), postId);
        return API.OK("게시글이 삭제 완료 되었습니다.");
    }

    // 목록조회
    @GetMapping
    public ResponseEntity<?> getAllNoticePosts(@PageableDefault(size = 10) Pageable pageable) {
        PagedResponse<PostResponse> follows = noticePostService.getAllPosts(null, pageable);
        return API.OK(follows);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchNoticePosts(
            @ModelAttribute PostSearchRequest request,
            @PageableDefault(size = 10) Pageable pageable) {
        PagedResponse<PostResponse> follows =
                noticePostService.searchPosts(null, request, pageable);
        return API.OK(follows);
    }
}
