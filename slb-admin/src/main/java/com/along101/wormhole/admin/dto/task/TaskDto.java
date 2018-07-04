package com.along101.wormhole.admin.dto.task;

import com.along.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.common.constant.OperationEnum;
import com.along101.wormhole.admin.dto.BaseDto;
import lombok.*;

/**
 * Created by yinzuolong on 2017/7/20.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDto extends BaseDto {

    private Long id;
    private Long slbId;
    private Long appSiteId;
    private Long appSiteVersion;
    private String appId;
    private String domainPort;
    private OperationEnum opsType;
    private Integer status;
    private String msg;

}
