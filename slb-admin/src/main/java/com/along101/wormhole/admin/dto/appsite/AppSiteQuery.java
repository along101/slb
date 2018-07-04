package com.along101.wormhole.admin.dto.appsite;

import lombok.Data;

/**
 * Created by yinzuolong on 2017/7/17.
 */
@Data
public class AppSiteQuery {

    private Long id;
    private Long slbId;
    private String appId;
    private String name;
    private Integer status;
    private String domain;
    private String path;
    private String group;
}
