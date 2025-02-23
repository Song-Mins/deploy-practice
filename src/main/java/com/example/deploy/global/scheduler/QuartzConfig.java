package com.example.deploy.global.scheduler;


import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail deleteUserJobDetail() {
        return JobBuilder.newJob(DeleteUserQuartzJob.class)
                .withIdentity("deleteUserJobDetail")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger deleteUserJobTrigger() {
        return TriggerBuilder.newTrigger()
                .forJob(deleteUserJobDetail())
                .withIdentity("deleteUserTrigger")
                // 매일 새벽 3시
                .withSchedule(CronScheduleBuilder.cronSchedule("0 40 1 * * ?"))
                .build();
    }
}
