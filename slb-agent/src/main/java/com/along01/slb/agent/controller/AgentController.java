package com.along01.slb.agent.controller;

import com.along01.slb.agent.domain.TaskAppStatusDo;
import com.along01.slb.agent.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AgentController {
	@Autowired
	private TaskService taskService;

	@GetMapping("/data")
	public TaskAppStatusDo getData() {
		return taskService.getData();
	}

	@GetMapping("/check")
	public String check() {
		return taskService.check();
	}

	@GetMapping("/hs")
	public String healtchCheck() {
		return "OK";
	}
}
