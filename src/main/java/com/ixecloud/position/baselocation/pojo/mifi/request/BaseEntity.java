package com.ixecloud.position.baselocation.pojo.mifi.request;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.UUID;

public class BaseEntity {

    private String id;

    private Long ts;

    @JSONField(name = "device_id")
    private String deviceId;


    public BaseEntity() {
        this.id = UUID.randomUUID().toString();
        this.ts = System.currentTimeMillis();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
