package com.along101.wormhole.admin.common.constant;

import java.util.Arrays;
import java.util.Optional;

/**
 * Created by yinzuolong on 2017/7/21.
 */
public enum TaskStatusEnum {
    INVALID(0, "无效"),
    SUCCESS(1, "成功"),
    RUNNING(2, "执行中"),
    FAILED(3, "失败"),
    OVERTIME(4, "超时");

    private int code;
    private String desc;

    TaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static Optional<TaskStatusEnum> findByCode(int code) {
        return Arrays.stream(TaskStatusEnum.values())
                .filter(taskStatus -> taskStatus.getCode() == code)
                .findFirst();
    }
}
