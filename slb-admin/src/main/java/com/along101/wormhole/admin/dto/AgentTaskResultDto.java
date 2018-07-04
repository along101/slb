package com.along101.wormhole.admin.dto;

import lombok.Data;

/**
 * Created by huangyinhuang on 7/28/2017.
 */
@Data
public class AgentTaskResultDto extends BaseDto {
    private Long id;
    private Long slbId;
    private String agentIp;
    private Long appSiteId;
    private Long appSiteVersion;
    private Long taskId;
    private String msg;
    private Integer status;
}
