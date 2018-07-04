package com.along101.wormhole.admin.dto;

import lombok.Data;

/**
 * Created by huangyinhuang on 7/28/2017.
 */
@Data
public class AgentAppSiteStatusDto extends BaseDto {

    private Long id;
    private Long slbId;
    private String agentIp;
    private Long appSiteId;
    private String domain;
    private Integer port;
    private Long currentAppSiteVersion;
    private Long agentAppSiteVersion;
    private Long currentTaskId;
    private Long agentTaskId;
    private String msg;
    private Integer status;

}
