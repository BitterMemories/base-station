package com.ixecloud.position.baselocation.service.mifi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.controller.BaseStationController;
import com.ixecloud.position.baselocation.domain.BaseLocation;
import com.ixecloud.position.baselocation.domain.Device;
import com.ixecloud.position.baselocation.domain.DeviceLocation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStationEliminateEntity;
import com.ixecloud.position.baselocation.pojo.mifi.response.AutoNaviEntity;
import com.ixecloud.position.baselocation.pojo.mifi.response.BaseStaticLocationEntity;
import com.ixecloud.position.baselocation.repository.BaseLocationRepository;
import com.ixecloud.position.baselocation.repository.DeviceLocationRepository;
import com.ixecloud.position.baselocation.repository.DeviceRepository;
import com.ixecloud.position.baselocation.service.mifi.BaseStationService;
import com.ixecloud.position.baselocation.service.mifi.DeviceService;
import com.ixecloud.position.baselocation.util.FormatDateTime;
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaseStationServiceImpl implements BaseStationService {

    private static final Logger logger = LoggerFactory.getLogger(BaseStationServiceImpl.class);

    @Autowired
    private HttpUtils httpUtils;

    @Autowired
    private DeviceLocationRepository deviceLocationRepository;

    @Autowired
    private BaseLocationRepository baseLocationRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RestTemplate restTemplate;

    @Transactional
    @Override
    public void AutoNaviBaseStation(BaseStation baseStation) {

        List<BaseStation.BaseStationInfo> baseStationInfoList = baseStation.getData();

        //存储上报得基站列表数据
        String locationUuid = UUID.randomUUID().toString();
        String currentTimeForCN = FormatDateTime.getCurrentTimeForCN();
        List<BaseLocation> baseLocationList = baseStationInfoList.stream().map(baseStationInfo -> {
            BaseLocation baseLocation = new BaseLocation();
            baseLocation.setCellId(baseStationInfo.getCellid());
            baseLocation.setLac(baseStationInfo.getLac());
            baseLocation.setMcc(baseStationInfo.getMcc());
            baseLocation.setMnc(baseStationInfo.getMnc());
            baseLocation.setFlag(baseStationInfo.getFlag());
            String dbm = baseStationInfo.getDbm();
            if(Integer.parseInt(dbm) > 0){
                int dbmInt = Integer.parseInt(dbm) * 2 - 133;
                dbm = String.valueOf(dbmInt);
            }
            baseLocation.setSignal(dbm);
            baseLocation.setLocationUuid(locationUuid);
            baseLocation.setCreateTime(currentTimeForCN);
            baseLocation.setDeviceId(baseStation.getId());
            return baseLocation;
        }).collect(Collectors.toList());
        baseLocationRepository.saveAll(baseLocationList);

        //按照信号值大小排序后定位
        List<BaseLocation> baseLocations = baseLocationList.stream().sorted((s, y) -> Integer.compare(Integer.parseInt(y.getSignal()), Integer.parseInt(s.getSignal()))).collect(Collectors.toList());
        AutoNaviEntity.LocationInfo locationInfo = autoNaviPosition(baseLocations);

        //存储设备地理位置信息
        DeviceLocation deviceLocation = new DeviceLocation();
        deviceLocation.setDeviceId(baseStation.getId());
        deviceLocation.setLocationUuid(locationUuid);
        BeanUtils.copyProperties(locationInfo, deviceLocation);
        String[] location = locationInfo.getLocation().split(",");
        deviceLocation.setLat(location[0]);
        deviceLocation.setLon(location[1]);
        deviceLocation.setCreateTime(currentTimeForCN);
        deviceLocation.setFlag(1);
        deviceLocationRepository.save(deviceLocation);
    }

    @Override
    public JSONObject getDeviceLocationInfo(String deviceId) {
        //查询设备信息
        Device device = deviceRepository.findDeviceByDeviceId(deviceId);
        JSONObject responseJson = JSON.parseObject(JSON.toJSONString(device));

        //查询设备地理位置信息
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
        responseJson.put("deviceLocation", deviceLocation);

        List<BaseLocation> baseLocationsMobile = new ArrayList<>();
        List<BaseLocation> baseLocationsUnicom = new ArrayList<>();
        List<BaseLocation> baseLocationsTelecom = new ArrayList<>();
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationsByDeviceIdOrderBySignalAsc(deviceId);
        baseLocationList.forEach(baseLocation -> {
            //信号数字百分比化
            DecimalFormat df = new DecimalFormat("0.00");
            if(Integer.parseInt(baseLocation.getSignal() ) >= -50){
                baseLocation.setSignal("1.00");
            }else {
                baseLocation.setSignal(df.format((110.00 - Math.abs(Integer.parseInt(baseLocation.getSignal()) + 50)) / 110.00));
            }

            //按照运营商不同对基站分组
            switch (baseLocation.getMnc()) {
                case "0":
                    baseLocationsMobile.add(baseLocation);
                    break;
                case "1":
                    baseLocationsUnicom.add(baseLocation);
                    break;
                case "11":
                    baseLocationsTelecom.add(baseLocation);
                    break;
            }
        });
        JSONObject operatorsJson = new JSONObject();
        operatorsJson.put("baseLocationsMobile", baseLocationsMobile);
        operatorsJson.put("baseLocationsUnicom", baseLocationsUnicom);
        operatorsJson.put("baseLocationsTelecom", baseLocationsTelecom);
        responseJson.put("baseLocationList", operatorsJson);
        return responseJson;
    }

    @Transactional
    @Override
    public void locationRefreshOperation(String deviceId) {
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
        if(ObjectUtils.isNotEmpty(deviceLocation)){
            boolean succeed = deviceService.gatherBaseStation(deviceId);
            if(succeed){
                deviceLocation.setFlag(0);
                deviceLocationRepository.save(deviceLocation);
            }
            if(succeed){
                logger.debug("locationRefreshOperation deviceID:{} location refresh operation succeed!", deviceId);
            }else {
                logger.debug("locationRefreshOperation deviceID:{} location refresh operation failure!", deviceId);
            }
        }
    }

    @Override
    public DeviceLocation locationRefreshData(String deviceId) {
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
        return deviceLocation;
    }

    @Override
    public AutoNaviEntity locationTest(String mmac, String[] macs) {

        //构建请求高德APIURL
        String url = new URIBuilder()
                .setScheme("http")
                .setHost("apilocate.amap.com")
                .setPath("/position")
                .setParameter("accesstype", "1")
                .setParameter("output", "json")
                .setParameter("mmac", mmac)
                .setParameter("macs", StringUtils.join(macs, "|"))
                .setParameter("key", "53b43fd0f88c5d98cb8dafeb4f98da82").toString();

        //请求高德地图API接口
        String responseString = httpUtils.doGet(url);
        AutoNaviEntity autoNaviEntity = JSON.parseObject(responseString, AutoNaviEntity.class);
        return autoNaviEntity;
    }

    @Override
    public List<BaseStaticLocationEntity> positionCheck(String deviceId) {
        List<BaseStaticLocationEntity> baseStaticLocationEntityList = new ArrayList<>();

        //对基站列表两两组合排列逐一定位排查
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationsByDeviceIdOrderBySignalAsc(deviceId);
        for (int i = 0; i < baseLocationList.size() - 1; i++) {
            for (int j = i + 1; j < baseLocationList.size() ; j++) {
                List<BaseLocation> baseLocations = new ArrayList<>();
                BaseLocation baseLocation1 = baseLocationList.get(i);
                baseLocations.add(baseLocation1);

                BaseLocation baseLocation2 = baseLocationList.get(j);
                baseLocations.add(baseLocation2);

                //构建请求高德APIURL
                String url = new URIBuilder()
                        .setScheme("http")
                        .setHost("apilocate.amap.com")
                        .setPath("/position")
                        .setParameter("accesstype", "0")
                        .setParameter("cdma", "0")
                        .setParameter("output", "json")
                        .setParameter("key", "53b43fd0f88c5d98cb8dafeb4f98da82")
                        .setParameter("bts", baseLocation1.toStringAutoNavi())
                        .setParameter("nearbts", baseLocation2.toStringAutoNavi()).toString();

                logger.debug("request autonavi url: {}", url);
                //请求高德地图API接口
                String responseString = httpUtils.doGet(url);
                logger.debug("response autonavi body: {}", responseString);
                AutoNaviEntity autoNaviEntity = JSON.parseObject(responseString, AutoNaviEntity.class);
                AutoNaviEntity.LocationInfo locationInfo = autoNaviEntity.getResult();

                BaseStaticLocationEntity baseStaticLocationEntity = new BaseStaticLocationEntity();
                BeanUtils.copyProperties(locationInfo, baseStaticLocationEntity);
                baseStaticLocationEntity.setBaseLocationList(baseLocations);
                baseStaticLocationEntityList.add(baseStaticLocationEntity);
            }
        }
        return baseStaticLocationEntityList;
    }

    @Transactional
    @Override
    public void removeBaseStationEliminate(BaseStationEliminateEntity baseStationEliminateEntity) {
        //剔除基站，设置Flag=2
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationsByDeviceIdAndCellIdAndMnc(baseStationEliminateEntity.getDeviceId(), baseStationEliminateEntity.getCellId(), baseStationEliminateEntity.getMnc());
        baseLocationList.forEach(baseLocation -> baseLocation.setFlag(2));
        baseLocationRepository.saveAll(baseLocationList);

        List<BaseLocation> baseLocations = baseLocationRepository.findBaseLocationsByDeviceIdOrderBySignalAsc(baseStationEliminateEntity.getDeviceId());
        AutoNaviEntity.LocationInfo autoNaviPosition = autoNaviPosition(baseLocations);

        //存储地理位置信息
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(baseStationEliminateEntity.getDeviceId());
        BeanUtils.copyProperties(autoNaviPosition, deviceLocation);
        String[] location = autoNaviPosition.getLocation().split(",");
        deviceLocation.setLat(location[0]);
        deviceLocation.setLon(location[1]);
        deviceLocation.setFlag(1);
        deviceLocationRepository.save(deviceLocation);
    }


    @Transactional
    @Override
    public void recoverBaseStationEliminate(BaseStationEliminateEntity baseStationEliminateEntity) {
        //重置基站
        baseLocationRepository.updateBaseLocationsResetFlag(baseStationEliminateEntity.getDeviceId());

        //恢复基站
        baseLocationRepository.updateBaseLocationsFlagByCellId(baseStationEliminateEntity.getDeviceId(), baseStationEliminateEntity.getDeviceId());

        //高德定位
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationsByDeviceIdOrderBySignalAsc(baseStationEliminateEntity.getDeviceId());
        AutoNaviEntity.LocationInfo autoNaviPosition = autoNaviPosition(baseLocationList);

        //存储地理位置信息
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(baseStationEliminateEntity.getDeviceId());
        BeanUtils.copyProperties(autoNaviPosition, deviceLocation);
        String[] location = autoNaviPosition.getLocation().split(",");
        deviceLocation.setLat(location[0]);
        deviceLocation.setLon(location[1]);
        deviceLocation.setFlag(1);
        deviceLocationRepository.save(deviceLocation);
    }

    @Override
    public JSONObject baseLocationScan(String deviceId) {

        Device device = deviceRepository.findDeviceByDeviceId(deviceId);
        if(device == null){
            BaseStationController.MAP.remove(deviceId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 3);
            jsonObject.put("message", "无此设备！");
            return jsonObject;
        }

        JSONArray jsonArray = baseStationScan(deviceId);

        if(jsonArray == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 4);
            jsonObject.put("message", "没有扫描到基站信息！");
            return jsonObject;
        }
        JSONObject jsonObjectResponse = autoNaviLocation(jsonArray);
        if(jsonObjectResponse.getInteger("code") == 1 && jsonObjectResponse.getString("msg").equals("succeed")){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 0);
            jsonObject.put("message", "OK");
            jsonObject.put("data", jsonObjectResponse.get("data"));
            return jsonObject;
        }else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", 5);
            jsonObject.put("message", "高德定位失败！");
            return jsonObject;
        }
    }

    private JSONObject autoNaviLocation(JSONArray jsonArray){
        String url = "http://172.20.180.80:30887/api/lbs/base-location/many";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Accept", "application/json");

        HttpEntity<JSONObject> responseEntity = restTemplate
                .exchange(url,
                        HttpMethod.POST,   //POST
                        new HttpEntity<String>(jsonArray.toJSONString(), headers),   //加入headers
                        JSONObject.class);  //body响应数据接收类型
        JSONObject jsonObject = responseEntity.getBody();
        return jsonObject;
    }

    private JSONArray baseStationScan(String deviceId){
        String url = "http://172.20.180.80:30034/api/capi/initiative/base-location/info";

        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization",
                "Basic " +
                        Base64.getEncoder()
                                .encodeToString("capi:capi".getBytes()));
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Accept", "application/json");

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("device_id", deviceId);

        HttpEntity<String> responseEntity = restTemplate
                .exchange(url,
                        HttpMethod.POST,   //POST
                        new HttpEntity<>(paramMap, headers),   //加入headers
                        String.class);  //body响应数据接收类型
        String entityBody = responseEntity.getBody();

        JSONObject jsonObject = JSONObject.parseObject(entityBody);
        JSONArray jsonArray = jsonObject.getJSONArray("data");

        if(jsonArray == null){
            return null;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            object.put("ci", object.getBigInteger("cellid"));
            object.remove("cellid");
            object.put("signal", object.getInteger("csq"));
            object.remove("csq");
        }

        return jsonArray;
    }

    @Override
    public List<BaseLocation> getBaseStationEliminate(String deviceId) {
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationsByDeviceIdAndFlag(deviceId, 2);
        return baseLocationList;
    }

    //高德API统一定位接口
    private AutoNaviEntity.LocationInfo autoNaviPosition(List<BaseLocation> baseLocationList){
        for (int i = 0; i < baseLocationList.size(); i++) {
            BaseLocation baseLocation = baseLocationList.get(i);
            if(baseLocation.getFlag() == 2){
                baseLocationList.remove(i);
                i--;
            }
        }

        StringBuilder bts = new StringBuilder();;
        StringBuilder nearbts = new StringBuilder();
        long array_size = baseLocationList.size();
        //long count = baseLocationList.stream().filter(m -> Integer.parseInt(m.getSignal()) >= -50).count();
        //array_size = count >= 2 ? count : 2;
        //array_size = baseLocationList.size() < 2 ? baseLocationList.size() : array_size;
        for (int i = 0; i < array_size; i++) {
            BaseLocation baseLocation = baseLocationList.get(i);
            baseLocation.setFlag(1);
            if(i == 0){
                bts.append(baseLocation.toStringAutoNavi());
            }else if(i == 1) {
                nearbts.append(baseLocation.toStringAutoNavi());
            }else {
                nearbts.append("|").append(baseLocation.toStringAutoNavi());
            }
        }

        logger.debug("request autonavi base-station param bts: {}\nnearbts: {}", bts, nearbts);

        //构建请求高德APIURL
        String url = new URIBuilder()
                .setScheme("http")
                .setHost("apilocate.amap.com")
                .setPath("/position")
                .setParameter("accesstype", "0")
                .setParameter("cdma", "0")
                .setParameter("output", "json")
                .setParameter("key", "53b43fd0f88c5d98cb8dafeb4f98da82")
                .setParameter("bts", bts.toString())
                .setParameter("nearbts", nearbts.toString()).toString();

        logger.debug("request autonavi url: {}", url);
        //请求高德地图API接口
        String responseString = httpUtils.doGet(url);
        logger.debug("response autonavi body: {}", responseString);
        AutoNaviEntity autoNaviEntity = JSON.parseObject(responseString, AutoNaviEntity.class);
        AutoNaviEntity.LocationInfo locationInfo = autoNaviEntity.getResult();
        return locationInfo;
    }
}
