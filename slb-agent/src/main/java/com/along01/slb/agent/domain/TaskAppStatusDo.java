package com.along01.slb.agent.domain;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;

import lombok.Data;

@Data
public class TaskAppStatusDo {
	private ConcurrentMap<String,TaskDomainStatusDo> taskDomains=Maps.newConcurrentMap();	
	private long taskId;
	private String nginxConf;
	private String nginxSbin;
}
