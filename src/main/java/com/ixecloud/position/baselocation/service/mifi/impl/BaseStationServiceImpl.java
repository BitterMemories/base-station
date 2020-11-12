package com.ixecloud.position.baselocation.service.mifi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ixecloud.position.baselocation.controller.DeviceController;
import com.ixecloud.position.baselocation.domain.BaseLocation;
import com.ixecloud.position.baselocation.domain.Device;
import com.ixecloud.position.baselocation.domain.DeviceLocation;
import com.ixecloud.position.baselocation.pojo.mifi.request.BaseStation;
import com.ixecloud.position.baselocation.pojo.mifi.response.AutoNaviEntity;
import com.ixecloud.position.baselocation.repository.BaseLocationRepository;
import com.ixecloud.position.baselocation.repository.DeviceLocationRepository;
import com.ixecloud.position.baselocation.repository.DeviceRepository;
import com.ixecloud.position.baselocation.service.mifi.BaseStationService;
import com.ixecloud.position.baselocation.service.mifi.DeviceService;
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
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

    @Override
    public void AutoNaviBaseStation(BaseStation baseStation) {

        //配置高德API参数
        StringBuilder bts = new StringBuilder();;
        StringBuilder nearbts = new StringBuilder();
        List<BaseStation.BaseStationInfo> baseStationInfoList = baseStation.getData();

        baseStationInfoList = baseStationInfoList.stream().sorted((s, y) -> Integer.compare(Integer.parseInt(y.getDbm()), Integer.parseInt(s.getDbm()))).collect(Collectors.toList());

        long array_size;
        long count = baseStationInfoList.stream().filter(m -> Integer.parseInt(m.getDbm()) >= -50).count();
        array_size = count >= 2 ? count : 2;

        for (int i = 0; i < array_size; i++) {
            BaseStation.BaseStationInfo baseStationInfo = baseStationInfoList.get(i);
            baseStationInfo.setFlag(1);
            if(i == 0){
                bts.append(baseStationInfo.getMcc())
                        .append(",").append(baseStationInfo.getMnc())
                        .append(",").append(baseStationInfo.getLac())
                        .append(",").append(baseStationInfo.getCellid())
                        .append(",").append(baseStationInfo.getDbm());
            }else if(i == 1) {
                nearbts.append(baseStationInfo.getMcc())
                        .append(",").append(baseStationInfo.getMnc())
                        .append(",").append(baseStationInfo.getLac())
                        .append(",").append(baseStationInfo.getCellid())
                        .append(",").append(baseStationInfo.getDbm());
            }else {
                nearbts.append("|").append(baseStationInfo.getMcc())
                        .append(",").append(baseStationInfo.getMnc())
                        .append(",").append(baseStationInfo.getLac())
                        .append(",").append(baseStationInfo.getCellid())
                        .append(",").append(baseStationInfo.getDbm());
            }
        }

        logger.debug("request autonavi base-station param bts: {}\nnearbts: {}", bts, nearbts);

        //构建请求高德APIURL
        String url = new URIBuilder()
                .setScheme("http")
                .setHost("apilocate.amap.com")
                .setPath("/position")
                .setParameter("accesstype", "0")
                .setParameter("imei", baseStation.getId())
                .setParameter("cdma", "0")
                .setParameter("output", "json")
                .setParameter("key", "53b43fd0f88c5d98cb8dafeb4f98da82")
                .setParameter("bts", bts.toString())
                .setParameter("nearbts", nearbts.toString()).toString();

        //请求高德地图API接口
        String responseString = httpUtils.doGet(url);
        AutoNaviEntity autoNaviEntity = JSON.parseObject(responseString, AutoNaviEntity.class);
        AutoNaviEntity.LocationInfo locationInfo = autoNaviEntity.getResult();

        //持久化地理位置信息
        DeviceLocation deviceLocation = new DeviceLocation();
        deviceLocation.setDeviceId(baseStation.getId());
        BeanUtils.copyProperties(locationInfo, deviceLocation);
        String[] location = locationInfo.getLocation().split(",");
        deviceLocation.setLat(location[0]);
        deviceLocation.setLon(location[1]);
        deviceLocation.setFlag(1);

        //删除之前的地理位置信息
        DeviceLocation dbDeviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceLocation.getDeviceId());
        if(ObjectUtils.isNotEmpty(dbDeviceLocation)){
            baseLocationRepository.deleteBaseLocationByDeviceId(deviceLocation.getDeviceId());
            deviceLocationRepository.deleteDeviceLocationByDeviceId(deviceLocation.getDeviceId());
        }

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
            baseLocation.setDeviceId(deviceLocation.getDeviceId());
            return baseLocation;
        }).collect(Collectors.toList());

        baseLocationRepository.saveAll(baseLocationList);
        deviceLocationRepository.save(deviceLocation);
    }

    @Override
    public JSONObject getDeviceLocationInfo(String deviceId) {
        //查询设备地理位置信息
        Device device = deviceRepository.findDeviceByDeviceId(deviceId);
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
        JSONObject responseJson = JSON.parseObject(JSON.toJSONString(device));
        responseJson.put("deviceLocation", deviceLocation);

        //查询基站列表并按照运营商不同分组
        List<BaseLocation> baseLocationsMobile = new ArrayList<>();
        List<BaseLocation> baseLocationsUnicom = new ArrayList<>();
        List<BaseLocation> baseLocationsTelecom = new ArrayList<>();
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationByDeviceId(deviceId);
        baseLocationList.forEach(baseLocation -> {
            DecimalFormat df = new DecimalFormat("0.00");
            if(Integer.parseInt(baseLocation.getSignal() ) >= -50){
                baseLocation.setSignal("1.00");
            }else {
                baseLocation.setSignal(df.format((110.00 - Math.abs(Integer.parseInt(baseLocation.getSignal()) + 50)) / 110.00));
            }

            if(baseLocation.getMnc().equals("0")){
                baseLocationsMobile.add(baseLocation);
            }else if(baseLocation.getMnc().equals("1")){
                baseLocationsUnicom.add(baseLocation);
            }else if(baseLocation.getMnc().equals("11")){
                baseLocationsTelecom.add(baseLocation);
            }
        });
        JSONObject operatorsJson = new JSONObject();
        operatorsJson.put("baseLocationsMobile", baseLocationsMobile);
        operatorsJson.put("baseLocationsUnicom", baseLocationsUnicom);
        operatorsJson.put("baseLocationsTelecom", baseLocationsTelecom);
        responseJson.put("baseLocationList", operatorsJson);
        return responseJson;
    }

    @Override
    public void locationRefreshOperation(String deviceId) {
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
        if(ObjectUtils.isNotEmpty(deviceLocation)){
            boolean succeed = deviceService.gatherBaseStation(deviceId);
            /*if(succeed){
                deviceLocation.setFlag(0);
                deviceLocationRepository.save(deviceLocation);
            }*/
            if(succeed){
                logger.debug("locationRefreshOperation deviceID:{} location refresh operation succeed!", deviceId);
            }
        }
    }

    @Override
    public DeviceLocation locationRefreshData(String deviceId) {
        DeviceLocation deviceLocation = deviceLocationRepository.findDeviceLocationByDeviceId(deviceId);
        return deviceLocation;
    }
}
