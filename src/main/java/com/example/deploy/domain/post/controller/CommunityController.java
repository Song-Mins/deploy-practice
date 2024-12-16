package com.example.deploy.domain.post.controller;


import com.example.deploy.domain.post.model.request.PostRequest;
import com.example.deploy.domain.post.model.request.PostSearchRequest;
import com.example.deploy.domain.post.model.response.PostResponse;
import com.example.deploy.domain.post.service.CommunityPostService;
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
@RequestMapping("/api/post/communities")
public class CommunityController {

    private final CommunityPostService communityPostService;

    @PreAuthorize("hasAnyRole('ROLE_INFLUENCER', 'ROLE_ENTERPRISER')")
    @PostMapping // 커뮤니티 게시글 생성
    public ResponseEntity<?> createCommunityPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestPart("data") PostRequest postRequest,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles) {
        PostResponse communityResponse =
                communityPostService.createPost(
                        customUserDetails.getUserId(), postRequest, imageFiles);
        return API.OK(communityResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_INFLUENCER', 'ROLE_ENTERPRISER')")
    @GetMapping("{postId}") // 커뮤니티 게시글 단건 조회
    public ResponseEntity<?> getCommunityPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @ModelAttribute PostSearchRequest postRequest) {
        PostResponse communityResponse =
                communityPostService.getPost(customUserDetails.getUserId(), postId, postRequest);
        return API.OK(communityResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_INFLUENCER', 'ROLE_ENTERPRISER')")
    @PatchMapping("/{postId}") // 커뮤니티 게시글 수정
    public ResponseEntity<?> updateCommunityPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @RequestPart("data") PostRequest postRequest,
            @RequestPart(value = "imageFile", required = false) List<MultipartFile> imageFiles) {
        PostResponse communityResponse =
                communityPostService.updatePost(
                        customUserDetails.getUserId(), postId, postRequest, imageFiles);
        return API.OK(communityResponse);
    }

    @PreAuthorize("hasAnyRole('ROLE_INFLUENCER', 'ROLE_ENTERPRISER')")
    @DeleteMapping("/{postId}") // 커뮤니티 게시글 삭제
    public ResponseEntity<?> deleteCommunityPost(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId) {

        communityPostService.deletePost(customUserDetails.getUserId(), postId);
        return API.OK("게시글이 삭제 완료 되었습니다.");
    }

    @PreAuthorize("hasAnyRole('ROLE_INFLUENCER', 'ROLE_ENTERPRISER')")
    @GetMapping
    public ResponseEntity<?> getAllCommunityPosts( // 커뮤니티 게시글 전체 목록 조회
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        PagedResponse<PostResponse> communities =
                communityPostService.getAllPosts(customUserDetails.getUserId(), pageable);
        return API.OK(communities);
    }

    @PreAuthorize("hasAnyRole('ROLE_INFLUENCER', 'ROLE_ENTERPRISER')")
    @GetMapping("/search")
    public ResponseEntity<?> searchCommunityPosts(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @ModelAttribute PostSearchRequest request,
            @PageableDefault(size = 10) Pageable pageable) {

        PagedResponse<PostResponse> communities =
                communityPostService.searchPosts(customUserDetails.getUserId(), request, pageable);
        return API.OK(communities);
    }
}
