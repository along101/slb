package com.along101.wormhole.admin.dto.task;

import lombok.Data;

@Data
public class TaskAppDto {
    private Long appSiteId;
    private String appId;
    private Long version;
    private Long taskId;
    //对于reload 不需要这些字段
    private String upstreamName;
    private String upstreamConf;
    //返回用的
    private String msg;
    private int status;
    //这两个暂时不用只是起标识作用
//	private boolean deleted;
//	private boolean hasChanged;
}
