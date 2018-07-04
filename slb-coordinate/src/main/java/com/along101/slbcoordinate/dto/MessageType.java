package com.along101.slbcoordinate.dto;

public enum MessageType {

    SUCCESS(0, "请求成功完成。"),
    ERROR(-1, "发现错误。"),
    UNKNOWN(-4, "未知错误。"),

    INVALID_ID(-1008, "发现无效的查询ID。");

    private Integer code;
    private String msg;

    MessageType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

}
