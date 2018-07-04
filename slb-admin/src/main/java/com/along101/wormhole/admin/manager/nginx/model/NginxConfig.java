package com.along101.wormhole.admin.manager.nginx.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NginxConfig {

    private NginxServer server;

    private List<NginxUpstream> upstreams = new ArrayList<>();

}
