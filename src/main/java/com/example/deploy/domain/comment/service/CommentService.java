package com.example.deploy.domain.comment.service;


import com.example.deploy.domain.comment.excepiton.CommentException;
import com.example.deploy.domain.comment.excepiton.errortype.CommentErrorCode;
import com.example.deploy.domain.comment.model.entity.Comment;
import com.example.deploy.domain.comment.model.request.CommentRequest;
import com.example.deploy.domain.comment.model.response.CommentResponse;
import com.example.deploy.domain.comment.model.response.CommentsAndRepliesResponse;
import com.example.deploy.domain.comment.repository.CommentRepository;
import com.example.deploy.domain.post.event.PostCommentEvent;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.repository.PostRepository;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.model.response.PagedResponse;
import com.example.deploy.global.type.S3PathPrefixType;
import com.example.deploy.global.util.S3Util;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final S3Util s3Util;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 댓글 size 10개로 페이지네이션, 대댓글 리스트 조회
     *
     * @param postId 댓글 조회할 게시글 ID
     * @return 해당하는 페이지의 댓글 리스트 및 페이지 정보
     */
    @Transactional(readOnly = true)
    public CommentsAndRepliesResponse getComments(Long postId, int page, int size) {
        // parent 컬럼이 null 인 데이터(대댓글이 아닌 댓글)만 조회
        Page<Comment> comments =
                commentRepository.findByPostIdAndDeletedFalseAndParentIsNull(
                        postId, PageRequest.of(page - 1, size));

        List<CommentResponse> commentResponseList =
                comments.get()
                        .map(
                                comment -> {
                                    String profileUrl =
                                            s3Util.selectImage(
                                                    comment.getUser().getProfileImage(),
                                                    S3PathPrefixType.S3_PROFILE_IMAGE_PATH
                                                            .toString());
                                    return CommentResponse.from(
                                            comment, comment.getUser().getNickname(), profileUrl);
                                })
                        .toList();

        // 대댓글 리스트 조회 (parentId 순 정렬)
        List<CommentResponse> replyList =
                findChildCommentsByCommentId(comments.get().map(Comment::getId).toList());

        PagedResponse<CommentResponse> pagedComments =
                new PagedResponse<>(
                        commentResponseList, comments.getTotalElements(), comments.getTotalPages());
        return new CommentsAndRepliesResponse(pagedComments, replyList);
    }

    /**
     * 댓글 작성(생성) 메서드
     *
     * @param request postId:작성할 댓글의 대상 게시글 ID, parentId:생성 대댓글의 부모 댓글 ID, content: 댓글 내용
     */
    @Transactional
    public void createComment(Long userId, Long postId, CommentRequest request) {
        User user = userRepository.getUserById(userId);
        Post post = postRepository.getPostById(postId);
        Comment parent = null;
        if (request.parentId() != null)
            parent =
                    commentRepository
                            .findByIdAndDeletedFalse(request.parentId())
                            .orElseThrow(
                                    () -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        Comment comment = Comment.from(request, user, post, parent);
        commentRepository.save(comment);
        eventPublisher.publishEvent(new PostCommentEvent(comment.getPost()));
    }

    /**
     * 댓글 수정 메서드
     *
     * @param request id:댓글 ID, postId:게시글 ID, parentId:부모 댓글 ID, content:댓글 내용
     */
    @Transactional
    public void updateComment(Long userId, Long commentId, CommentRequest request) {
        Comment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(
                                () -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        comment.updateBy(userId, request);
    }

    /**
     * 댓글 삭제 메서드, deleted 필드 값을 1로 update
     *
     * @param userId 요청 클라이언트 userId
     * @param commentId 삭제할 댓글 id
     */
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        Comment comment =
                commentRepository
                        .findById(commentId)
                        .orElseThrow(
                                () -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        comment.deleteBy(userId);
    }

    /**
     * 대댓글 조회 메서드 (주어진 list id와 parentId가 일치하는 comment 조회)
     *
     * @param commentIds 부모 댓글 id 리스트
     * @return 대댓글 리스트
     */
    private List<CommentResponse> findChildCommentsByCommentId(List<Long> commentIds) {
        List<Comment> replies =
                commentRepository.findByParentIdInAndDeletedFalseOrderByParentId(commentIds);
        return replies.stream()
                .map(
                        comment -> {
                            String profileUrl =
                                    s3Util.selectImage(
                                            comment.getUser().getProfileImage(),
                                            S3PathPrefixType.S3_PROFILE_IMAGE_PATH.toString());
                            return CommentResponse.from(
                                    comment, comment.getUser().getNickname(), profileUrl);
                        })
                .toList();
    }
}
