package com.along101.wormhole.admin.dto;

import com.along101.wormhole.admin.common.exception.SlbServiceException;
import lombok.Data;

/**
 * common web service response, which provide uniform format of response code and message
 *
 * @author huang
 * @date 2016/12/16
 */
@Data
public class Response<T> {

    private Integer code;
    private String message;
    private T detail;


    public static <T> Response<T> success(T detail) {
        Response<T> response = new Response<>();
        response.code = 0;
        response.message = "success";
        response.detail = detail;
        return response;
    }

    public static <T> Response<T> error(String message) {
        Response<T> response = new Response<>();
        response.code = 1;
        response.message = message;
        response.detail = null;
        return response;
    }

    public static <T> Response<T> error(SlbServiceException e) {
        Response<T> response = new Response<>();
        response.code = 1;
        response.message = e.getMessage();
        response.detail = null;
        return response;
    }

    public static <T> Response<T> error(Throwable e) {
        Response<T> response = new Response<>();
        response.code = -1;
        response.message = e.getMessage();
        response.detail = null;
        return response;
    }

}
