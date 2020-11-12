package com.ixecloud.position.baselocation.service.ros.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.common.ApiConstant;
import com.ixecloud.position.baselocation.domain.Manager;
import com.ixecloud.position.baselocation.enums.RosEnum;
import com.ixecloud.position.baselocation.pojo.BaseEntity;
import com.ixecloud.position.baselocation.repository.ManagerRepository;
import com.ixecloud.position.baselocation.service.ros.DeviceInfoService;
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private ManagerRepository managerRepository;

    @Override
    public JSONObject getControlInfo(BaseEntity baseEntity) {
        Manager deviceManage = managerRepository.findManagerByDeviceId(baseEntity.getDeviceId());
        JSONObject entityJson = JSON.parseObject(JSON.toJSONString(baseEntity));
        JSONObject responseJson = callRos(RosEnum.GET_CONTROL, entityJson.toJSONString(), deviceManage, JSONObject.class);
        return responseJson;
    }

    @Override
    public JSONObject deviceReboot(BaseEntity baseEntity) {
        Manager deviceManage = managerRepository.findManagerByDeviceId(baseEntity.getDeviceId());
        JSONObject entityJson = JSON.parseObject(JSON.toJSONString(baseEntity));
        JSONObject responseJson = callRos(RosEnum.REBOOT, entityJson.toJSONString(), deviceManage, JSONObject.class);
        return responseJson;
    }

    @Override
    public JSONObject getDeviceStatus(BaseEntity baseEntity) {
        Manager deviceManage = managerRepository.findManagerByDeviceId(baseEntity.getDeviceId());
        JSONObject entityJson = JSON.parseObject(JSON.toJSONString(baseEntity));
        JSONObject responseJson = callRos(RosEnum.GET_STATUS, entityJson.toJSONString(), deviceManage, JSONObject.class);
        return responseJson;
    }


    public <T> T callRos(RosEnum rosEnums, String requestJson, Manager manager, Class<T> clazz){
        String uri = ApiConstant.ROS_URL + rosEnums.getUri();
        String methodType = rosEnums.getMethodType();
        JSONObject jsonObject = JSON.parseObject(requestJson);
        jsonObject.put("deviceid",jsonObject.getString("deviceId") );
        jsonObject.remove("deviceId");
        jsonObject.put("methodType", methodType);
        jsonObject.put("device_info", manager);
        String responseJson = httpUtils.post(jsonObject, uri);
        return JSON.parseObject(responseJson, clazz);
    }
}
