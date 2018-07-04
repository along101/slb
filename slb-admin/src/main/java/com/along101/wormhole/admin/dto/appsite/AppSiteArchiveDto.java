package com.along101.wormhole.admin.dto.appsite;

import lombok.Data;

import java.util.List;

/**
 * Created by yinzuolong on 2017/7/20.
 */
@Data
public class AppSiteArchiveDto {

    private Long id;

    private Long slbId;

    private Long appSiteId;

    private String appId;

    private Long version;

    private Integer flag;

    private String domain;

    private Integer port;

    private String domainPort;

    private AppSiteDto appSiteDto;

    private List<AppSiteDto> domainAppSiteDtos;
}
