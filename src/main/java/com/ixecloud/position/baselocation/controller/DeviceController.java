package com.ixecloud.position.baselocation.controller;

import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.common.Response;
import com.ixecloud.position.baselocation.enums.ResponseCode;
import com.ixecloud.position.baselocation.pojo.BaseEntity;
import com.ixecloud.position.baselocation.service.ros.DeviceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@RestController
@RequestMapping(value = "device")
public class DeviceController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);



    @Autowired
    private DeviceInfoService deviceInfoService;



    @PostMapping(value = "reboot")
    public Response restartDevice(@RequestBody BaseEntity baseEntity){
        JSONObject responseJson = deviceInfoService.deviceReboot(baseEntity);
        if(!responseJson.getString("code").equals("1003")){
            return new Response(ResponseCode.INTERNAL_SERVER_ERROR, responseJson.getString("message"));
        }
        return new Response(ResponseCode.OK, responseJson.get("data"));
    }

    @GetMapping(value = "status")
    public Response getDeviceStatus(@RequestParam(value = "deviceId") String deviceId){
        JSONObject responseJson = deviceInfoService.getDeviceStatus(new BaseEntity(deviceId));
        if(!responseJson.getString("code").equals("0")){
            return new Response(ResponseCode.INTERNAL_SERVER_ERROR, responseJson.getString("message"));
        }
        return new Response(ResponseCode.OK, responseJson.get("data"));
    }

    @GetMapping(value = "start-time")
    public Response startTime(@RequestParam(value = "deviceId") String deviceId){
        JSONObject responseJson = deviceInfoService.getControlInfo(new BaseEntity(deviceId));
        if(!responseJson.getString("code").equals("0")){
            return new Response(ResponseCode.NOT_FOUND, responseJson.getString("msg"));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String uptime = responseJson.getJSONObject("data").getString("uptime");
        String[] uptimes = uptime.split(" ");
        logger.debug("uptime size: {},uptimes: {}", uptimes.length, uptimes);
        int length = uptimes.length;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if(length == 1){
            String strings = uptimes[0];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }else if(length == 2){
            String strings = uptimes[0];
            int minute = Integer.parseInt(strings.replace("MINUTE", ""));
            cal.add(Calendar.MINUTE, -minute);

            strings = uptimes[1];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }else if(length == 3){
            String strings = uptimes[0];
            int hour = Integer.parseInt(strings.replace("HOUR", ""));
            cal.add(Calendar.HOUR, -hour);

            strings = uptimes[1];
            int minute = Integer.parseInt(strings.replace("MINUTE", ""));
            cal.add(Calendar.MINUTE, -minute);

            strings = uptimes[2];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }else if(length == 4){
            String strings = uptimes[0];
            int week = Integer.parseInt(strings.replace("WEEK", ""));
            cal.add(Calendar.WEEK_OF_MONTH, -week);

            strings = uptimes[1];
            int hour = Integer.parseInt(strings.replace("HOUR", ""));
            cal.add(Calendar.HOUR, -hour);

            strings = uptimes[2];
            int minute = Integer.parseInt(strings.replace("MINUTE", ""));
            cal.add(Calendar.MINUTE, -minute);

            strings = uptimes[3];
            int second = Integer.parseInt(strings.replace("SECOND", ""));
            cal.add(Calendar.SECOND, -second);
        }
        responseJson.getJSONObject("data").put("start-time", sdf.format(cal.getTime()));

        return new Response(ResponseCode.OK, responseJson.get("data"));
    }

}
