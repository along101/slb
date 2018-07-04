package com.along01.slb.agent.dto.taskconfget;

import java.util.List;

import com.along01.slb.agent.dto.BaseResponse;
import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class TaskConfGetResponse extends BaseResponse {
	private List<TaskDomainDto> taskDomains=Lists.newArrayList();	
	private String nginxConf;
	private String nginxSbin;	
	private long lastTaskId;
}
