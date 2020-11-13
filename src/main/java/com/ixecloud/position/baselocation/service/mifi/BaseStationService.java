package com.ixecloud.position.baselocation.service.mifi;

import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.domain.DeviceLocation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStation;
import com.ixecloud.position.baselocation.pojo.mifi.response.AutoNaviEntity;

public interface BaseStationService {

    void AutoNaviBaseStation(BaseStation baseStation);

    JSONObject getDeviceLocationInfo(String deviceId);

    void locationRefreshOperation(String deviceId);

    DeviceLocation locationRefreshData(String deviceId);

    AutoNaviEntity locationTest(String mmac, String[] macs);

}
