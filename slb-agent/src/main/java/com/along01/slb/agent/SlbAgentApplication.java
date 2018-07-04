package com.along01.slb.agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.along01.slb.agent.service.TaskService;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class SlbAgentApplication {	
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(SlbAgentApplication.class, args);
		TaskService taskService = context.getBean(TaskService.class);
		taskService.start();
	}
}
