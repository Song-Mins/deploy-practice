package com.example.deploy.global.util;


import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

@Configuration
@Slf4j(topic = "Email Log")
public class MailUtil {

    @Value("${email.id}") private String id;

    @Value("${email.pw}") private String pw;

    /**
     * @param email 보낼 이메일 주소
     * @param title 제목
     * @param content 내용 @Auther Domae-back-end
     */
    @Async("emailPoolTask")
    public void sendMessage(String email, String title, String content) {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.naver.com");
        javaMailSender.setUsername(id);
        javaMailSender.setPassword(pw);

        javaMailSender.setPort(465);

        javaMailSender.setJavaMailProperties(getMailProperties());

        MimeMessage m = javaMailSender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(m, "UTF-8");

        try {
            h.setFrom(id + "@naver.com");
            h.setTo(email);
            h.setSubject(title);
            h.setText(content);
            javaMailSender.send(m);
        } catch (Exception e) {
            log.error("'{}' 내용의 이메일 전송 실패", content);
        }

        log.info("'{}' 내용의 이메일 전송 성공", content);
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.debug", "false");
        properties.setProperty("mail.smtp.ssl.trust", "smtp.naver.com");
        properties.setProperty("mail.smtp.ssl.enable", "true");
        return properties;
    }
}
