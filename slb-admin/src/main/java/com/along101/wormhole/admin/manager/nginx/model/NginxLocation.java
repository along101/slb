package com.along101.wormhole.admin.manager.nginx.model;

import lombok.Data;

@Data
public class NginxLocation {
    private String pattern;
    private String upstream;
}
