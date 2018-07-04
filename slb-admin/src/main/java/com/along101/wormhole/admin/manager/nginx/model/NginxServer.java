package com.along101.wormhole.admin.manager.nginx.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NginxServer {

    private List<NginxLocation> locations = new ArrayList<>();

    private int listen = 80;

    private String serverName;

}
