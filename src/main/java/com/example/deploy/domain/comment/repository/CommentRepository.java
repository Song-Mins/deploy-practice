package com.example.deploy.domain.comment.repository;


import com.example.deploy.domain.comment.model.entity.Comment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUserId(Long userId);

    /**
     * 삭제되지 않고 대댓글이 아닌 댓글 리스트를 페이지네이션으로 반환
     *
     * @param postId 대상 게시글의 ID
     * @param pageRequest 페이지 번호
     * @return 해당하는 페이지의 댓글 리스트 및 페이지 정보
     */
    Page<Comment> findByPostIdAndDeletedFalseAndParentIsNull(Long postId, PageRequest pageRequest);

    /**
     * 해당하는 ID의 삭제되지 않은 댓글 레코드를 반환
     *
     * @param id 대상 댓글 ID
     * @return 일치하는 ID의 댓글 레코드
     */
    Optional<Comment> findByIdAndDeletedFalse(Long id);

    /**
     * 매개변수로 주어진 Id와 parentId가 일치하는 대댓글을 조회
     *
     * @param parentIdList 부모 댓글의 idList
     * @return 부모 댓글에 대한 대댓글 리스트
     */
    List<Comment> findByParentIdInAndDeletedFalseOrderByParentId(List<Long> parentIdList);

    // 해당 사용자가 작성한 댓글들 삭제
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Comment c WHERE c.parent.id = :parentId")
    void deleteByParentId(Long parentId);
}
