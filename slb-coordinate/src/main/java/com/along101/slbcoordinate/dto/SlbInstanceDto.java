package com.along101.slbcoordinate.dto;

import lombok.Data;

@Data
public class SlbInstanceDto {
	private String name;
	// 表示appid
	private transient long appSiteId;
	private String ip;
	private Integer port;
	// 1 表示up，0 表示down
	private transient Integer status;

	public String getIpPort() {
		return ip + "_" + port;
	}
}
