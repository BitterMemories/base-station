package com.ixecloud.position.baselocation.service.mifi;

import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.domain.BaseLocation;
import com.ixecloud.position.baselocation.domain.DeviceLocation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStationEliminateEntity;
import com.ixecloud.position.baselocation.pojo.mifi.response.AutoNaviEntity;
import com.ixecloud.position.baselocation.pojo.mifi.response.BaseStaticLocationEntity;

import java.util.List;

public interface BaseStationService {

    void AutoNaviBaseStation(BaseStation baseStation);

    JSONObject getDeviceLocationInfo(String deviceId);

    void locationRefreshOperation(String deviceId);

    DeviceLocation locationRefreshData(String deviceId);

    AutoNaviEntity locationTest(String mmac, String[] macs);

    List<BaseStaticLocationEntity> positionCheck(String deviceId);

    //基站剔除
    void removeBaseStationEliminate(BaseStationEliminateEntity baseStationEliminateEntity);

    List<BaseLocation> getBaseStationEliminate(String deviceId);

    //基站恢复
    void recoverBaseStationEliminate(BaseStationEliminateEntity baseStationEliminateEntity);
}
