package com.along101.slbcoordinate.dto;

import lombok.Data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
public class AppSiteServerDto {
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
    private Date insertTime;
    private String insertBy;
    private Date updateTime;
    private String updateBy;

}
