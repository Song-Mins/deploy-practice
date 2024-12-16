package com.example.deploy.domain.comment.model.response;


import com.example.deploy.domain.comment.model.entity.Comment;
import com.example.deploy.domain.user.model.entity.User;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        Long parentId,
        String userName,
        String userProfileImage,
        String content,
        LocalDateTime createdAt) {
    public static CommentResponse from(Comment comment, String nickName, String imageUrl) {
        Long parentId = null;
        if (comment.getParent() != null) parentId = comment.getParent().getId();
        User user = comment.getUser();

        return new CommentResponse(
                comment.getId(),
                parentId,
                nickName,
                imageUrl,
                comment.getContent(),
                comment.getCreatedAt());
    }
}
