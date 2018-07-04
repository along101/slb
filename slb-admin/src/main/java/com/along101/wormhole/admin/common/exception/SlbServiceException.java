package com.along101.wormhole.admin.common.exception;

/**
 * Created by huangyinhuang on 7/17/2017.
 */
public class SlbServiceException extends RuntimeException {


    public SlbServiceException(Throwable cause) {
        super(cause);
    }

    public SlbServiceException(String message) {
        super(message);
    }

    public SlbServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public static SlbServiceException newException(Throwable cause, String message, Object... params) {
        SlbServiceException sre;
        if (params != null && params.length > 0) {
            String formatMessage = String.format(message, params);
            sre = new SlbServiceException(formatMessage, cause);
        } else {
            sre = new SlbServiceException(message);
        }
        return sre;
    }

    public static SlbServiceException newException(String message, Object... params) {
        SlbServiceException sre;
        if (params != null && params.length > 0) {
            String formatMessage = String.format(message, params);
            if (params[params.length - 1] instanceof Throwable) {
                sre = new SlbServiceException(formatMessage, (Throwable) params[params.length - 1]);
            } else {
                sre = new SlbServiceException(formatMessage);
            }
        } else {
            sre = new SlbServiceException(message);
        }
        return sre;
    }

}
