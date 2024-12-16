package com.example.deploy.global.batch.job;


import com.example.deploy.domain.campaign.model.entity.Campaign;
import com.example.deploy.domain.campaign.repository.CampaignRepository;
import com.example.deploy.domain.comment.model.entity.Comment;
import com.example.deploy.domain.comment.repository.CommentRepository;
import com.example.deploy.domain.post.model.entity.Post;
import com.example.deploy.domain.post.repository.PostRepository;
import com.example.deploy.domain.user.model.entity.User;
import com.example.deploy.domain.user.repository.UserRepository;
import com.example.deploy.global.batch.listener.SlackNotificationListener;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.TransientDataAccessException;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class DeleteUserConfiguration {

    private final JobRepository jobRepository;
    private final SlackNotificationListener slackNotificationListener;
    private final PlatformTransactionManager transactionManager;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CampaignRepository campaignRepository;
    private final DataSource dataSource;

    @Bean
    public Job deleteUserJob() throws Exception {
        return new JobBuilder("deleteUserJob", jobRepository)
                .start(deleteUserStep())
                .listener(slackNotificationListener)
                .build();
    }

    @Bean
    public Step deleteUserStep() throws Exception {
        return new StepBuilder("deleteUserStep", jobRepository)
                .<Long, Long>chunk(100, transactionManager)
                .reader(userItemReader())
                .writer(userItemWriter())
                .faultTolerant()
                // 데이터베이스에 일시적으로 접근할 수 없는 경우
                .retry(TransientDataAccessException.class)
                .retryLimit(3) // 최대 재시도 횟수
                .build();
    }

    @Bean
    public JdbcPagingItemReader<Long> userItemReader() throws Exception {
        JdbcPagingItemReader<Long> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(10);

        // queryProvider 설정
        MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
        queryProvider.setSelectClause("SELECT id");
        queryProvider.setFromClause("FROM user");
        queryProvider.setWhereClause("WHERE is_deleted = true AND deleted_at < :deletedAt");
        queryProvider.setSortKeys(Map.of("id", Order.ASCENDING));

        reader.setQueryProvider(queryProvider);
        reader.setParameterValues(Map.of("deletedAt", LocalDate.now().minusYears(1)));

        // RowMapper 설정
        reader.setRowMapper((rs, rowNum) -> rs.getLong("id"));

        return reader;
    }

    @Bean
    public ItemWriter<Long> userItemWriter() {
        return userIds -> {
            for (Long userId : userIds) {

                // 삭제된 사용자를 의미하는 더미데이터 가져오기
                User deletedUserDummy = userRepository.getUserById(-1L);

                // 영구삭제될 사용자와 관련된 정보를 더미데이터로 대체
                List<Campaign> campaignList = campaignRepository.findByUserId(userId);
                for (Campaign campaign : campaignList) {
                    campaign.change(deletedUserDummy);
                }
                List<Post> postList = postRepository.findByUserId(userId);
                for (Post post : postList) {
                    post.change(deletedUserDummy);
                }
                List<Comment> commentList = commentRepository.findByUserId(userId);
                for (Comment comment : commentList) {
                    comment.change(deletedUserDummy);
                }

                // 사용자 삭제 - ON DELETE CASCADE 인 데이터는 같이 삭제됨
                userRepository.deleteById(userId);
            }
        };
    }
}
