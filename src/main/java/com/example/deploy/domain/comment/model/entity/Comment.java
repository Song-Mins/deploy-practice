package com.example.deploy.domain.comment.model.entity;


import com.example.deploy.domain.comment.excepiton.CommentException;
import com.example.deploy.domain.comment.excepiton.errortype.CommentErrorCode;
import com.example.deploy.domain.comment.model.request.CommentRequest;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.global.model.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Comment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> children;

    private String content;

    @Column(name = "is_deleted")
    private boolean deleted;

    public static Comment from(CommentRequest request, User user, Post post, Comment parent) {
        return Comment.builder()
                .user(user)
                .post(post)
                .parent(parent)
                .content(request.content())
                .deleted(false)
                .build();
    }

    public void updateBy(Long userId, CommentRequest commentRequest) {
        if (this.user.isNotSame(userId)) {
            throw new CommentException(CommentErrorCode.COMMENT_AUTHOR_MISMATCH);
        }
        this.content = commentRequest.content();
    }

    public void deleteBy(Long userId) {
        if (this.user.isNotSame(userId)) {
            throw new CommentException(CommentErrorCode.COMMENT_AUTHOR_MISMATCH);
        }
        this.deleted = true;
    }

    public void change(User user) {
        this.user = user;
    }
}
