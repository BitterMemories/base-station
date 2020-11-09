package com.ixecloud.position.baselocation.controller;

import com.ixecloud.position.baselocation.common.Response;
import com.ixecloud.position.baselocation.enums.ResponseCode;
import com.ixecloud.position.baselocation.service.mifi.DeviceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "position")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @PostMapping(value = "device")
    public Response gatherBaseStation(@RequestParam("deviceId") String deviceId){

        if(StringUtils.isEmpty(deviceId)){
            logger.debug("参数不合法：{}", deviceId);
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }

        //boolean auth = deviceService.deviceAuth(deviceId);
        //if(auth){
            //下达采集基站信息指令
            boolean succeed = deviceService.gatherBaseStation(deviceId);
            if(succeed){
                return new Response(ResponseCode.OK);
            }else {
                return new Response(ResponseCode.GATHER_BASE_STATION_FAILED, "采集基站信息指令下发失败！");
            }
        //}else {
        //    logger.debug("输入deviceId不合法：{}", deviceId);
        //    return new Response(ResponseCode.NOT_FOUND, "deviceId不合法！");
       //}
    }

}
