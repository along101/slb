package com.along101.wormhole.admin.dto.task;

import java.util.List;

import com.google.common.collect.Lists;
import com.along101.wormhole.admin.dto.BaseResponse;
import lombok.Data;

@Data
public class TaskConfGetResponse extends BaseResponse {
    private List<TaskDomainDto> taskDomains = Lists.newArrayList();
    private String nginxConf;
    private String nginxSbin;
    private long lastTaskId;
}
