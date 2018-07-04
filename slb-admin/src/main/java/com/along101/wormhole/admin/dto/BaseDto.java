package com.along101.wormhole.admin.dto;

import lombok.Data;

import java.util.Date;

/**
 * Created by yinzuolong on 2017/7/13.
 */
@Data
public class BaseDto {
    protected Date insertTime;
    protected String insertBy;
    protected Date updateTime;
    protected String updateBy;
}
