package com.along01.slb.agent.dto.taskconfget;

import lombok.Data;

@Data
public class TaskAppDto {
	private String appId;	
	private long appSiteId;
	private Long version;	
	private Long taskId;
	private String upstreamName;
	private String upstreamConf;
	private String msg;
	private int status;		
	//这两个暂时不用只是起标识作用
//	private boolean deleted;
//	private boolean hasChanged;
}
