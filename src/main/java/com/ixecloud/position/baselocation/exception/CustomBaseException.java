package com.ixecloud.position.baselocation.exception;

import com.ixecloud.position.baselocation.enums.ErrorCode;

import java.io.Serializable;

/**
 *  自定义封装异常类
 */
public class CustomBaseException extends RuntimeException implements Serializable {

    /** 错误码 */
    protected final ErrorCode errorCode;

    /**
     * 构造通用异常
     * @param errorCode 错误码
     * @param message 详细描述
     */
    public CustomBaseException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
