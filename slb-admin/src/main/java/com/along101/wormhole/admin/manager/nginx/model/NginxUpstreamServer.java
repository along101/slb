package com.along101.wormhole.admin.manager.nginx.model;

import lombok.Data;

@Data
public class NginxUpstreamServer {

    private String ip;

    private int port = 80;

    private int weight = 50;

    private int maxFails = 3;

    private int failTimeout = 30;

}
