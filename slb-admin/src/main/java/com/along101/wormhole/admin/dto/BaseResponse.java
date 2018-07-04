package com.along101.wormhole.admin.dto;

import lombok.Data;

@Data
public class BaseResponse {
    protected boolean isSuc = true;
    protected String failMsg;
}
