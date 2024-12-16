package com.example.deploy.global.batch.listener;


import java.util.HashMap;
import java.util.Map;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class SlackNotificationListener implements JobExecutionListener {

    @Value("${slack.url}") private String slackWebhookUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void beforeJob(JobExecution jobExecution) {
        // Job 시작 알림
        sendSlackNotification(jobExecution, "실행을 시작합니다.");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        // Job 실패 알림
        if (jobExecution.getStatus().isUnsuccessful()) {
            sendSlackNotification(jobExecution, "실행이 실패했습니다.");
        }
        // Job 성공 알림
        else {
            sendSlackNotification(jobExecution, "성공적으로 완료되었습니다.");
        }
    }

    private void sendSlackNotification(JobExecution jobExecution, String event) {
        String jobName = jobExecution.getJobInstance().getJobName();
        String status = jobExecution.getStatus().toString();
        String message = String.format("Job *%s* %s 상태: %s", jobName, event, status);

        // Slack 메시지 생성
        Map<String, String> slackMessage = new HashMap<>();
        slackMessage.put("text", message);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(slackMessage, headers);

        // Slack에 메시지 전송
        restTemplate.postForEntity(slackWebhookUrl, entity, String.class);
    }
}
