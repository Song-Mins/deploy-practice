package com.example.deploy.global.util;


import com.example.deploy.global.model.request.DiscordMessageRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "Discord Logs")
public class DiscordUtil {

    @Value("${discord.url}") private String url;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * @param content 내용 @Auther Domae-back-end
     */
    public void sendMessage(String content) {
        HttpEntity<DiscordMessageRequest> request =
                new HttpEntity<>(new DiscordMessageRequest(prefixContent(content)));
        ResponseEntity response = restTemplate.postForEntity(url, request, ResponseEntity.class);

        if (response.getStatusCode().is2xxSuccessful()) log.info("성공적으로 '{}' 내용 전달 완료", content);
        else log.error("'{}' 내용 전달 실패 / 에러코드 : '{}'", content, response.getStatusCode().value());
    }

    private String prefixContent(String content) {
        return content;
    }
}
