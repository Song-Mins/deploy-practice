package com.example.deploy.domain.comment.controller;

import com.example.deploy.domain.comment.model.request.CommentRequest;
import com.example.deploy.domain.comment.model.response.CommentsAndRepliesResponse;
import com.example.deploy.domain.comment.service.CommentService;
import com.example.deploy.domain.user.config.model.CustomUserDetails;
import com.example.deploy.global.api.API;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post/{post_id}/comments")
public class CommentController {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<?> getComments(
            @PathVariable(value = "post_id") Long postId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        CommentsAndRepliesResponse response = commentService.getComments(postId, page, size);
        return API.OK(response);
    }

    @PostMapping
    public ResponseEntity<?> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable(value = "post_id") Long postId,
            @RequestBody CommentRequest request) {
        commentService.createComment(customUserDetails.getUserId(), postId, request);
        return API.OK();
    }

    @PatchMapping("/{comment_id}")
    public ResponseEntity<?> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable(value = "comment_id") Long commentId,
            @RequestBody CommentRequest request) {
        commentService.updateComment(customUserDetails.getUserId(), commentId, request);
        return API.OK();
    }

    @DeleteMapping("/{comment_id}")
    public ResponseEntity<?> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable(value = "comment_id") Long commentId) {
        commentService.deleteComment(customUserDetails.getUserId(), commentId);
        return API.OK();
    }
}
