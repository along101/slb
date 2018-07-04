package com.along101.wormhole.admin.common.constant;

/**
 * Created by yinzuolong on 2017/7/19.
 */
public enum OnlineStatusEnum {

    ONLINE(1), OFFLINE(0);

    private int value;

    OnlineStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
