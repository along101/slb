package com.along101.wormhole.admin.dto.operation;

import lombok.Data;

/**
 * Created by yinzuolong on 2017/8/7.
 */
@Data
public class UpdateServersIpPortDto {

    private String name;
    private String ip;
    private Integer port;
}
