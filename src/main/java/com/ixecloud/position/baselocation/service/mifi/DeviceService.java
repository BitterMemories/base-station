package com.ixecloud.position.baselocation.service.mifi;

public interface DeviceService {

    /**
     * 在进行设备绑定前进行的设备ID验证，确保设备合法性。
     * @param deviceId - 设备id
     */
    boolean deviceAuth(String deviceId);

    /**
     * 获取混合基站定位数据
     * @param deviceId - 设备id
     */
    boolean gatherBaseStation(String deviceId);




}
