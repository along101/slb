package com.along101.wormhole.admin.common.constant;

/**
 * Created by yinzuolong on 2017/7/21.
 */
public enum OperationTypeEnum {

    UP(1),
    DOWN(0);
    private int code;

    OperationTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
