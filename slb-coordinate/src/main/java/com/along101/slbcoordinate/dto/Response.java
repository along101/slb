package com.along101.slbcoordinate.dto;


/**
 * common web service response, which provide uniform format of response code and message
 *
 * @author huang
 * @date 2016/12/16
 */
public class Response<T> {

    private Integer code;
    private String message;
    private T detail;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getDetail() {
        return detail;
    }

    public void setDetail(T result) {
        this.detail = result;
    }
}
