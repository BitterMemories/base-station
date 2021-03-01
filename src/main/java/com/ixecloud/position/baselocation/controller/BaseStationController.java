package com.ixecloud.position.baselocation.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.common.Response;
import com.ixecloud.position.baselocation.domain.BaseLocation;
import com.ixecloud.position.baselocation.domain.DeviceLocation;
import com.ixecloud.position.baselocation.enums.ResponseCode;
import com.ixecloud.position.baselocation.pojo.BaseEntity;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStationEliminateEntity;
import com.ixecloud.position.baselocation.pojo.mifi.response.AutoNaviEntity;
import com.ixecloud.position.baselocation.pojo.mifi.response.BaseStaticLocationEntity;
import com.ixecloud.position.baselocation.service.mifi.BaseStationService;
import com.ixecloud.position.baselocation.service.mifi.DeviceService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping(value = "base-station")
public class BaseStationController {

    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private BaseStationService baseStationService;

    public static Map<String, Long> MAP = new HashMap<>();

    //接收上报的基站数据
    @PostMapping(value = "notice")
    public Response notice(@RequestBody String requestJson) throws UnsupportedEncodingException {
        requestJson = URLDecoder.decode(requestJson, "UTF-8");
        requestJson = requestJson.substring(0, requestJson.length() - 1);
        logger.debug("base-station push request body: {}", requestJson);

        BaseStation baseStation = JSON.parseObject(requestJson, BaseStation.class);

        baseStationService.AutoNaviBaseStation(baseStation);

        return new Response(ResponseCode.OK);

    }


    //获取设备位置信息和基站列表
    @GetMapping(value = "position-info")
    public Response getBaseStationPositionInfo(@RequestParam(value = "deviceId") String deviceId) {
        if(StringUtils.isEmpty(deviceId)){
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }
        JSONObject responseJson = baseStationService.getDeviceLocationInfo(deviceId);
        return new Response(ResponseCode.OK, responseJson);

    }

    //暂未使用，可手动调用
    @PostMapping(value = "instruction")
    public Response gatherBaseStation(@RequestBody String requestJson){
        if(StringUtils.isEmpty(requestJson)){
            logger.debug("参数不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "参数不能为空！");
        }

        JSONObject requestJsonObject = JSON.parseObject(requestJson);
        String deviceId = requestJsonObject.getString("deviceId");

        //下达采集基站信息指令
        boolean succeed = deviceService.gatherBaseStation(deviceId);
        if(succeed){
            return new Response(ResponseCode.OK);
        }else {
            return new Response(ResponseCode.GATHER_BASE_STATION_FAILED, "采集基站信息指令下发失败！");
        }
    }

    //位置刷新，会触发采集基站指令
    @PostMapping(value = "location-refresh")
    public Response locationRefreshOperation(@RequestBody String requestJson){
        if(StringUtils.isEmpty(requestJson)){
            logger.debug("参数不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "参数不能为空！");
        }

        JSONObject requestJsonObject = JSON.parseObject(requestJson);
        String deviceId = requestJsonObject.getString("deviceId");

        baseStationService.locationRefreshOperation(deviceId);
        return new Response(ResponseCode.OK);

    }

    //获取设备位置信息
    @GetMapping(value = "location-refresh")
    public Response locationRefreshDate(@RequestParam(value = "deviceId") String deviceId){
        if(StringUtils.isEmpty(deviceId)){
            logger.debug("deviceId不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }
        DeviceLocation deviceLocation = baseStationService.locationRefreshData(deviceId);
        return new Response(ResponseCode.OK, deviceLocation);
    }

    //定位排查
    @PostMapping(value = "position-check")
    public Response positionCheck(@RequestBody BaseEntity baseEntity){
        if(ObjectUtils.isEmpty(baseEntity)){
            logger.debug("deviceId不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }
        List<BaseStaticLocationEntity> baseStaticLocationEntityList = baseStationService.positionCheck(baseEntity.getDeviceId());
        return new Response(ResponseCode.OK, baseStaticLocationEntityList);
    }

    //基站剔除
    @DeleteMapping(value = "eliminate")
    public Response removeBaseStationEliminate(@RequestBody BaseStationEliminateEntity baseStationEliminateEntity){
        if(ObjectUtils.isEmpty(baseStationEliminateEntity)){
            logger.debug("deviceId不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }
        baseStationService.removeBaseStationEliminate(baseStationEliminateEntity);
        return new Response(ResponseCode.OK);
    }


    //获得已经被剔除的基站列表
    @GetMapping(value = "eliminate")
    public Response getBaseStationEliminate(@RequestParam(value = "deviceId") String deviceId){
        if(ObjectUtils.isEmpty(deviceId)){
            logger.debug("deviceId不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }
        List<BaseLocation> baseLocationList = baseStationService.getBaseStationEliminate(deviceId);
        return new Response(ResponseCode.OK, baseLocationList);
    }


    //基站恢复
    @PostMapping(value = "eliminate")
    public Response baseStationEliminate(@RequestBody BaseStationEliminateEntity baseStationEliminateEntity){
        if(ObjectUtils.isEmpty(baseStationEliminateEntity)){
            logger.debug("deviceId不能为空!");
            return new Response(ResponseCode.BAD_REQUEST, "deviceId不能为空！");
        }
        baseStationService.recoverBaseStationEliminate(baseStationEliminateEntity);
        return new Response(ResponseCode.OK);
    }






    @GetMapping(value = "location-test")
    public Response locationTest(@RequestParam(value = "mmac") String mmac, @RequestParam(value = "macs") String macs){
        AutoNaviEntity autoNaviEntity = baseStationService.locationTest(mmac, macs.split("\\|"));
        AutoNaviEntity.LocationInfo locationInfo = autoNaviEntity.getResult();
        return new Response(ResponseCode.OK, locationInfo);
    }

    @GetMapping(value = "freeze-time")
    public JSONObject freezeTime(@RequestParam(value = "deviceId") String deviceId){
        JSONObject jsonObject = new JSONObject();
        long timeMillis = System.currentTimeMillis();
        Long aLong = BaseStationController.MAP.get(deviceId);
        if (aLong == null){
            aLong = 0L;
        }
        long current = (timeMillis - aLong) / 1000;
        if(current < 90){
            jsonObject.put("code", 1);
            jsonObject.put("message", "冷冻时间还剩：" + (90 - current));
            jsonObject.put("data", (90 - current));
        }else {
            jsonObject.put("code", 0);
            jsonObject.put("message", "OK");
        }
        return jsonObject;
    }


    @GetMapping(value = "activate")
    public JSONObject activate(@RequestParam(name = "deviceId") String deviceId){
        long timeMillis = System.currentTimeMillis();
        Long aLong = BaseStationController.MAP.get(deviceId);
        if (aLong == null){
            aLong = 0L;
        }
        long current = (timeMillis - aLong) / 1000;
        JSONObject jsonObject = new JSONObject();
        if(current < 90){
            jsonObject.put("code", 1);
            jsonObject.put("message", "冷冻时间还剩：" + (90 - current));
            jsonObject.put("data", (90 - current));
            return jsonObject;
        }else {
            BaseStationController.MAP.put(deviceId, System.currentTimeMillis());
            jsonObject = baseStationService.baseLocationScan(deviceId);

        }
        return jsonObject;
    }

}
