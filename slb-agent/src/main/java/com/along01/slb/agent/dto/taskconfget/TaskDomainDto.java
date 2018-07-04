package com.along01.slb.agent.dto.taskconfget;

import java.util.List;

import lombok.Data;

@Data
public class TaskDomainDto {	
	//private transient String domainPort;	
	private String domain;
	private int port;
	private String conf;
	private int optType;//1表示表示reload，2，表示dyup 
	private List<TaskAppDto> taskApps;
	public String getDomainPort(){
		return domain+"_"+port;
	}
	
}
