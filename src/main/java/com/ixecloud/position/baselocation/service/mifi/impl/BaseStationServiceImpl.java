package com.ixecloud.position.baselocation.service.mifi.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
import com.ixecloud.position.baselocation.util.HttpUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    @Override
    public void AutoNaviBaseStation(BaseStation baseStation) {

        List<BaseStation.BaseStationInfo> baseStationInfoList = baseStation.getData();

        //存储上报得基站列表数据
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
            baseLocation.setDeviceId(baseStation.getId());
            return baseLocation;
        }).collect(Collectors.toList());
        baseLocationRepository.deleteBaseLocationByDeviceId(baseStation.getId());
        baseLocationRepository.saveAll(baseLocationList);

        //按照信号值大小排序后定位
        List<BaseLocation> baseLocations = baseLocationList.stream().sorted((s, y) -> Integer.compare(Integer.parseInt(y.getSignal()), Integer.parseInt(s.getSignal()))).collect(Collectors.toList());
        AutoNaviEntity.LocationInfo locationInfo = autoNaviPosition(baseLocations);

        //存储设备地理位置信息
        DeviceLocation deviceLocation = new DeviceLocation();
        deviceLocation.setDeviceId(baseStation.getId());
        BeanUtils.copyProperties(locationInfo, deviceLocation);
        String[] location = locationInfo.getLocation().split(",");
        deviceLocation.setLat(location[0]);
        deviceLocation.setLon(location[1]);
        deviceLocation.setFlag(1);

        deviceLocationRepository.deleteDeviceLocationByDeviceId(deviceLocation.getDeviceId());
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
        List<BaseLocation> baseLocationList = baseLocationRepository.findBaseLocationsByDeviceIdAndCellId(baseStationEliminateEntity.getDeviceId(), baseStationEliminateEntity.getCellId());
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
            }
        }

        StringBuilder bts = new StringBuilder();;
        StringBuilder nearbts = new StringBuilder();
        long array_size;
        long count = baseLocationList.stream().filter(m -> Integer.parseInt(m.getSignal()) >= -50).count();
        array_size = count >= 2 ? count : 2;
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
