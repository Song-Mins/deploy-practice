package com.example.deploy.domain.post.repository;


import com.example.deploy.domain.post.exception.PostException;
import com.example.deploy.domain.post.exception.errortype.PostErrorCode;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.model.entity.enums.CategoryType;
import com.example.deploy.domain.user.model.entity.enums.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    default Post getPostById(Long id) {
        return findById(id).orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    // 게시글 단건 조회
    Optional<Post> findByIdAndDeletedFalse(Long id);

    default Post getPostByIdAndDeletedFalse(Long id) {
        return findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
    }

    @Query(
            "SELECT p FROM Post p LEFT JOIN FETCH p.user u WHERE p.categoryType = 'COMMUNITY' AND u.role = :role AND p.deleted = false ORDER BY p.id DESC")
    Page<Post> findCommunityPostsByRole(@Param("role") Role role, Pageable pageable);

    // 커뮤니티 전체 게시글 조회 (최신순 정렬)
    @Query(
            "SELECT p FROM Post p LEFT JOIN FETCH p.postMeta WHERE p.categoryType = :categoryType AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Post> findByCategoryType(
            @Param("categoryType") CategoryType categoryType, Pageable pageable);

    // 검색 기능: 제목, 작성자 이름, 내용에 대해 검색 (카테고리 필터 포함, 최신순 정렬)
    @Query(
            "SELECT p FROM Post p LEFT JOIN FETCH p.user u LEFT JOIN FETCH p.postMeta WHERE p.categoryType = :categoryType AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword% OR u.nickname LIKE %:keyword%) AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Post> searchByKeyword(
            @Param("categoryType") CategoryType categoryType,
            @Param("keyword") String keyword,
            Pageable pageable);

    @Modifying
    @Query(
            "UPDATE PostMeta pm SET pm.viewCount = pm.viewCount + :viewCount WHERE pm.post.id = :postId")
    void updateViewCount(@Param("postId") Long postId, @Param("viewCount") int viewCount);

    @Modifying
    @Query(
            "UPDATE PostMeta pm SET pm.commentCount = pm.commentCount + :commentCount WHERE pm.post.id = :postId")
    void updateCommentCount(@Param("postId") Long postId, @Param("commentCount") int commentCount);

    @Modifying
    @Query("DELETE FROM Post p WHERE p.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    List<Post> findByUserId(Long userId);
}
