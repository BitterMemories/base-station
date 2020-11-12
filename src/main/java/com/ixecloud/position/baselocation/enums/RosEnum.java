package com.ixecloud.position.baselocation.enums;

public enum RosEnum {


    REBOOT("/reboot","reboot"),
    GET_STATUS("/status", "status"),
    GET_CONTROL("/control", "control");

    private String uri;

    private String methodType;


    RosEnum(String uri, String methodType){
        this.uri = uri;
        this.methodType = methodType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }
}
