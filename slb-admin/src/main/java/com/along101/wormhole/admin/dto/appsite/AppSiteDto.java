package com.along101.wormhole.admin.dto.appsite;

import com.along.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import lombok.Data;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Data
public class AppSiteDto extends BaseDto {

    private Long id;
    private Long slbId;
    private String appId;
    private String name;
    private String note;
    private Integer status;
    private String domain;
    private Integer port;
    private String path;
    private String group;
    private String loadbalance;
    private String healthUri;
    private String serverDirective;
    private String upstreamDirective;
    private Long onlineVersion;
    private Long offlineVersion;
    private String locationDirective;
    private List<AppSiteServerDto> appSiteServers;

}
