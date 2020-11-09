package com.ixecloud.position.baselocation.enums;

public enum ErrorCode {

    /**
     * 错误请求
     */
    INVALID_REQUEST(500, "Invalid request"),
    /**
     * 参数验证错误
     */
    INVALID_ARGUMENT(400, "Validation failed for argument"),
    /**
     * 未找到资源
     */
    NOT_FOUND(404,"NotFound"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(520, "Unknown server internal error.");

    private int code;

    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
