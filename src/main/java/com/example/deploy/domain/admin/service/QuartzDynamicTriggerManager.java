package com.example.deploy.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuartzDynamicTriggerManager {

	private final Scheduler scheduler;

	// 기존 트리거의 스케줄을 업데이트하는 메서드
	public void updateTrigger(String triggerName, String newCronExpression) throws SchedulerException {
		TriggerKey triggerKey = new TriggerKey(triggerName, "userCleanupGroup");

		// 기존 트리거 조회
		CronTrigger oldTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
		if (oldTrigger == null) {
			System.out.println("Trigger not found");
			throw new SchedulerException();
		}

		// 새로운 Cron 스케줄로 트리거 빌드
		CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(newCronExpression);
		CronTrigger newTrigger = oldTrigger.getTriggerBuilder()
			.withSchedule(scheduleBuilder)
			.build();

		// 스케줄러에 업데이트 적용
		scheduler.rescheduleJob(triggerKey, newTrigger);
	}
}
