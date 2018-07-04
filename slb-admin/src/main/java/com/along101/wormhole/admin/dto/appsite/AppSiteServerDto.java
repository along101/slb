package com.along101.wormhole.admin.dto.appsite;

import com.along.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import com.along101.wormhole.admin.dto.BaseDto;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinzuolong on 2017/7/11.
 */
@Data
public class AppSiteServerDto extends BaseDto {

    private Long id;
    private String name;
    private Long appSiteId;
    private String hostName;
    private String ip;
    private Integer port;
    private Integer weight;
    private Integer maxFails;
    private Integer failTimeout;
    private String tag;
    private Integer status;
    private Map<String, Integer> statusDetail = new HashMap<>();

}
