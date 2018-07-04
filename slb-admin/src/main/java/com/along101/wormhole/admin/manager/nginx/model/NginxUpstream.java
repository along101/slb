package com.along101.wormhole.admin.manager.nginx.model;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NginxUpstream {
    private String name;

    private String lbStrategy;

    private List<NginxUpstreamServer> servers = new ArrayList<>();
}
