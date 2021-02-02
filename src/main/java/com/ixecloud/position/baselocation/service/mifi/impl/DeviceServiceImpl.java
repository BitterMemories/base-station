package com.ixecloud.position.baselocation.service.mifi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.common.ApiConstant;
import com.ixecloud.position.baselocation.domain.Device;
import com.ixecloud.position.baselocation.domain.DeviceLocation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseEntity;
import com.ixecloud.position.baselocation.repository.DeviceLocationRepository;
import com.ixecloud.position.baselocation.repository.DeviceRepository;
import com.ixecloud.position.baselocation.service.mifi.DeviceService;
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceLocationRepository deviceLocationRepository;

    @Autowired
    private HttpUtils httpUtils;


    @Override
    public boolean deviceAuth(String deviceId) {
        BaseEntity requestEntity = new BaseEntity();
        requestEntity.setDeviceId(deviceId);
        com.ixecloud.position.baselocation.pojo.mifi.response.BaseEntity responseEntity = httpUtils.mifiPost(ApiConstant.MIFI_DEVICE_AUTH, JSON.toJSONString(requestEntity), com.ixecloud.position.baselocation.pojo.mifi.response.BaseEntity.class);
        return responseEntity.getResult() == 0;
    }

    @Override
    public boolean gatherBaseStation(String deviceId) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("id", deviceId);
        requestJson.put("number", "12");
        String responseString = httpUtils.post(requestJson.toJSONString(), ApiConstant.MIFI_BASE_STATION_URL);
        JSONObject responseJson = JSONObject.parseObject(responseString);
        String result = responseJson.getString("result");
        boolean success = StringUtils.equals("success", result);
        if(success){
            Device dbDevice = deviceRepository.findDeviceByDeviceId(deviceId);
            if(ObjectUtils.isNotEmpty(dbDevice)){
                /*DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
                if(ObjectUtils.isNotEmpty(deviceLocation)){
                    deviceLocation.setFlag(0);
                    deviceLocationRepository.save(deviceLocation);
                }*/
                return true;
            }
            Device device = new Device();
            device.setDeviceId(deviceId);
            deviceRepository.save(device);
            return true;
        }else {
            return false;
        }
    }
}
