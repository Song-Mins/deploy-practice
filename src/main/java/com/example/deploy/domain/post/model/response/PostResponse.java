package com.example.deploy.domain.post.model.response;


import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.model.entity.enums.CategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String authorNickName; // 작성자 닉네임
    private String authorProfileImageUrl;
    private String title;
    private String content;
    private String noticeBoardType;
    private String categoryType;
    private List<String> attachedFileUrls;
    private LocalDateTime createdAt;
    private Long viewCount;
    private Long commentCount;
    private String contentPreview; // 글 내용 미리보기
    private Long previousPostId;
    private Long nextPostId;

    public static PostResponse responseWithContentPreview(Post post, String profileImageUrl) {

        return PostResponse.builder()
                .id(post.getId())
                .authorNickName(post.getUser().getNickname())
                .authorProfileImageUrl(profileImageUrl)
                .title(post.getTitle())
                .content(post.getContent())
                .noticeBoardType(post.getCategoryType().getDisplayName())
                .categoryType(determinePostCategoryType(post))
                .createdAt(post.getCreatedAt())
                .viewCount(post.getPostMeta().getViewCount())
                .commentCount(post.getPostMeta().getCommentCount())
                .contentPreview(getContentPreview(post.getContent()))
                .build();
    }

    public static PostResponse responseWithoutContentPreview(
            Post post,
            String profileImageUrl,
            List<String> imageUrls,
            Long previousPostId,
            Long nextPostId) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorNickName(post.getUser().getNickname())
                .authorProfileImageUrl(profileImageUrl)
                .noticeBoardType(post.getCategoryType().getDisplayName())
                .categoryType(determinePostCategoryType(post))
                .attachedFileUrls(imageUrls)
                .createdAt(post.getCreatedAt())
                .viewCount(post.getPostMeta().getViewCount())
                .commentCount(post.getPostMeta().getCommentCount())
                .previousPostId(previousPostId)
                .nextPostId(nextPostId)
                .build();
    }

    // HTML 태그는 무시하고 미리보기 생성
    private static String getContentPreview(String content) {
        int previewLength = 100; // 미리보기 길이 설정 (예시: 100자)
        String plainTextContent = content.replaceAll("<[^>]*>", ""); // HTML 태그 제거
        return plainTextContent.length() > previewLength
                ? plainTextContent.substring(0, previewLength) + "..."
                : plainTextContent;
    }

    private static String determinePostCategoryType(Post post) {
        if (post.getCategoryType().equals(CategoryType.COMMUNITY)) {
            return post.getCommunityType().getDisplayName();
        } else if (post.getCategoryType().equals(CategoryType.FOLLOW)) {
            return post.getFollowType().getDisplayName();
        }
        return null;
    }
}
