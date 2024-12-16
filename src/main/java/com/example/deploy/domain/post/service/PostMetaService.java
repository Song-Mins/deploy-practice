package com.example.deploy.domain.post.service;


import com.example.deploy.domain.post.repository.PostRepository;
import com.example.deploy.global.scheduler.exception.SchedulerException;
import com.example.deploy.global.scheduler.exception.errortype.SchedulerErrorCode;
import com.example.deploy.global.type.RedisPrefixType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMetaService {

    private final PostRepository postRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void incrementViewCount(Long postId) {
        String key = RedisPrefixType.POST_VIEW_COUNT.toString() + postId;
        redisTemplate.opsForValue().increment(key, 1); // 원자적으로 조회수 증가
    }

    public void incrementCommentCount(Long postId) {
        String key = RedisPrefixType.POST_COMMENT_COUNT.toString() + postId;
        redisTemplate.opsForValue().increment(key, 1);
    }

    @Transactional
    public void updatePostViewCount(String key) {
        try {
            Long postId = extractPostIdFromKey(key, RedisPrefixType.POST_VIEW_COUNT);
            String viewCountStr = redisTemplate.opsForValue().get(key);

            if (viewCountStr != null) {
                int viewCount = Integer.parseInt(viewCountStr);
                redisTemplate.delete(key);

                postRepository.updateViewCount(postId, viewCount);
            }
        } catch (Exception e) {
            throw new SchedulerException(SchedulerErrorCode.SYNC_FAILED);
        }
    }

    @Transactional
    public void updatePostCommentCount(String key) {
        try {
            Long postId = extractPostIdFromKey(key, RedisPrefixType.POST_COMMENT_COUNT);
            String commentCountStr = redisTemplate.opsForValue().get(key);

            if (commentCountStr != null) {
                int commentCount = Integer.parseInt(commentCountStr);
                redisTemplate.delete(key);

                postRepository.updateCommentCount(postId, commentCount);
            }
        } catch (Exception e) {
            throw new SchedulerException(SchedulerErrorCode.SYNC_FAILED);
        }
    }

    private Long extractPostIdFromKey(String key, RedisPrefixType prefixType) {
        return Long.valueOf(key.replace(prefixType.toString(), ""));
    }
}
