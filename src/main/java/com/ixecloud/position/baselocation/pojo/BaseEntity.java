package com.ixecloud.position.baselocation.pojo;

import org.apache.commons.lang3.builder.ToStringBuilder;

public class BaseEntity {

    private String deviceId;

    public BaseEntity() {
    }

    public BaseEntity(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("deviceId", deviceId).toString();
    }
}
