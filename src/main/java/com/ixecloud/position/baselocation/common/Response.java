package com.ixecloud.position.baselocation.common;

import com.ixecloud.position.baselocation.enums.ResponseCode;

import java.io.Serializable;

public class Response implements Serializable {


    private final int code;

    private final String message;

    private Object data;

    public Response(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public Response(ResponseCode responseCode, Object data) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
