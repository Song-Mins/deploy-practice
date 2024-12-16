package com.example.deploy.domain.admin.controller;

import com.example.deploy.domain.admin.model.request.UpdateTriggerRequest;
import com.example.deploy.domain.admin.service.QuartzDynamicTriggerManager;
import com.example.deploy.global.api.API;
import lombok.RequiredArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/scheduler")
public class SchedulerController {

	private final QuartzDynamicTriggerManager triggerManager;

	@PatchMapping("/trigger/{triggerName}")
	public ResponseEntity<?> updateTrigger(@PathVariable String triggerName,
		@RequestBody UpdateTriggerRequest updateTriggerRequest) throws SchedulerException {

		triggerManager.updateTrigger(triggerName, updateTriggerRequest.newCronExpression());
		return API.OK("스케줄러 변경 완료");
	}
}

