package com.ixecloud.position.baselocation.service.mifi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.common.ApiConstant;
import com.ixecloud.position.baselocation.enums.ErrorCode;
import com.ixecloud.position.baselocation.exception.CustomBaseException;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseEntity;
import com.ixecloud.position.baselocation.service.mifi.DeviceService;
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

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
        JSONObject responseJson = httpUtils.mifiPost(ApiConstant.MIFI_BASE_STATION_URL, requestJson.toJSONString(), JSONObject.class);
        String result = responseJson.getString("result");
        return StringUtils.equals("success", result);
    }
}
