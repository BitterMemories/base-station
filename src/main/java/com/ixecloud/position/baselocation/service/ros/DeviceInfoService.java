package com.ixecloud.position.baselocation.service.ros;

import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.pojo.BaseEntity;

public interface DeviceInfoService {

    //取开机时间
    JSONObject getControlInfo(BaseEntity baseEntity);

    //设备重启
    JSONObject deviceReboot(BaseEntity baseEntity);

    //获取设备状态
    JSONObject getDeviceStatus(BaseEntity baseEntity);
}
