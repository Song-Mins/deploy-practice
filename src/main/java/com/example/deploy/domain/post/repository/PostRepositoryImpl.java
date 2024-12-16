package com.example.deploy.domain.post.repository;



import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.model.entity.QPost;
import com.example.deploy.domain.post.model.entity.enums.CategoryType;
import com.example.deploy.domain.post.model.entity.enums.CommunityType;
import com.example.deploy.domain.post.model.entity.enums.FollowType;
import com.example.deploy.domain.user.model.entity.enums.Role;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long findPreviousPost(
            Long postId,
            Role role,
            CategoryType categoryType,
            CommunityType communityType,
            FollowType followType,
            String keyword) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();

        if (categoryType != null) {
            builder.and(post.categoryType.eq(categoryType));

            if (categoryType == CategoryType.COMMUNITY) {
                builder.and(post.user.role.eq(role));
            }
        }

        if (communityType != null) {
            builder.and(post.communityType.eq(communityType));
        }

        if (followType != null) {
            builder.and(post.followType.eq(followType));
        }

        if (keyword != null) {
            builder.and(post.title.containsIgnoreCase(keyword));
        }

        builder.and(post.deleted.isFalse());

        var query =
                queryFactory
                        .selectFrom(post)
                        .where(post.id.gt(postId), builder)
                        .orderBy(post.id.asc())
                        .fetchFirst();

        return (query == null) ? null : query.getId();
    }

    @Override
    public Long findNextPost(
            Long postId,
            Role role,
            CategoryType categoryType,
            CommunityType communityType,
            FollowType followType,
            String keyword) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();

        if (categoryType != null) {
            builder.and(post.categoryType.eq(categoryType));
        }

        if (communityType != null) {
            builder.and(post.communityType.eq(communityType));

            if (categoryType == CategoryType.COMMUNITY) {
                builder.and(post.user.role.eq(role));
            }
        }

        if (followType != null) {
            builder.and(post.followType.eq(followType));
        }

        if (keyword != null) {
            builder.or(post.title.containsIgnoreCase(keyword))
                    .or(post.content.containsIgnoreCase(keyword))
                    .or(post.user.nickname.containsIgnoreCase(keyword));
        }

        builder.and(post.deleted.isFalse());

        var query =
                queryFactory
                        .selectFrom(post)
                        .where(post.id.lt(postId), builder)
                        .orderBy(post.id.desc())
                        .fetchFirst();

        return (query == null) ? null : query.getId();
    }

    @Override
    public Page<Post> searchCommunityPost(
            Role role, String keyword, CommunityType communityType, Pageable pageable) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder keywordBuilder = new BooleanBuilder();

        builder.and(post.categoryType.eq(CategoryType.COMMUNITY))
                .and(post.user.role.eq(role))
                .and(post.deleted.isFalse());

        if (communityType != null) {
            builder.and(post.communityType.eq(communityType));
        }

        if (keyword != null) {
            keywordBuilder
                    .or(post.title.containsIgnoreCase(keyword))
                    .or(post.content.containsIgnoreCase(keyword))
                    .or(post.user.nickname.containsIgnoreCase(keyword));
        }

        builder.and(keywordBuilder);

        var query =
                queryFactory
                        .selectFrom(post)
                        .where(builder, keywordBuilder)
                        .orderBy(post.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize());

        List<Post> results = query.fetch();
        long total = queryFactory.selectFrom(post).where(builder).fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<Post> searchFollowPost(String keyword, FollowType followType, Pageable pageable) {
        QPost post = QPost.post;

        BooleanBuilder builder = new BooleanBuilder();
        BooleanBuilder keywordBuilder = new BooleanBuilder();

        builder.and(post.categoryType.eq(CategoryType.FOLLOW)).and(post.deleted.isFalse());

        if (followType != null) {
            builder.and(post.followType.eq(followType));
        }

        if (keyword != null) {
            keywordBuilder
                    .or(post.title.containsIgnoreCase(keyword))
                    .or(post.content.containsIgnoreCase(keyword))
                    .or(post.user.nickname.containsIgnoreCase(keyword));
        }

        builder.and(keywordBuilder);

        var query =
                queryFactory
                        .selectFrom(post)
                        .where(builder, keywordBuilder)
                        .orderBy(post.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize());

        List<Post> results = query.fetch();
        long total = queryFactory.selectFrom(post).where(builder).fetchCount();

        return new PageImpl<>(results, pageable, total);
    }
}
