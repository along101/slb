package com.along01.slb.agent.domain;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.along01.slb.agent.dto.taskconfget.TaskAppDto;

import lombok.Data;

@Data
public class TaskDomainStatusDo {
	private String domainPort;
	private String conf;
	private ConcurrentMap<String,TaskAppDto> taskApps=Maps.newConcurrentMap();
}
