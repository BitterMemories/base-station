package com.ixecloud.position.baselocation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "device_location")
public class DeviceLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "location_uuid")
    private String locationUuid;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "poi")
    private String poi;

    @Column(name = "adcode")
    private String adcode;

    @Column(name = "street")
    private String street;

    @Column(name = "`desc`")
    private String desc;

    @Column(name = "`country`")
    private String country;

    @Column(name = "`type`")
    private String type;

    @Column(name = "`lat`")
    private String lat;

    @Column(name = "lon")
    private String lon;

    @Column(name = "road")
    private String road;

    @Column(name = "radius")
    private String radius;

    @Column(name = "citycode")
    private String citycode;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Column(name = "create_time", columnDefinition = "TIMESTAMP")
    private String createTime;

    @Column(name = "flag")
    private Integer flag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
        this.radius = radius;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getLocationUuid() {
        return locationUuid;
    }

    public void setLocationUuid(String locationUuid) {
        this.locationUuid = locationUuid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("deviceId", deviceId)
                .append("locationUuid", locationUuid)
                .append("city", city)
                .append("province", province)
                .append("poi", poi)
                .append("adcode", adcode)
                .append("street", street)
                .append("desc", desc)
                .append("country", country)
                .append("type", type)
                .append("lat", lat)
                .append("lon", lon)
                .append("road", road)
                .append("radius", radius)
                .append("citycode", citycode)
                .append("createTime", createTime)
                .append("flag", flag).toString();
    }
}
