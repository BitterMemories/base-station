package com.ixecloud.position.baselocation.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "base_location")
public class BaseLocation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "location_uuid")
    private String locationUuid;

    @Column(name = "lac")
    private String lac;

    @Column(name = "cell_id")
    private String cellId;

    @Column(name = "mcc")
    private String mcc;

    @Column(name = "mnc")
    private String mnc;

    @Column(name = "`signal`")
    private String signal;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @Column(name = "create_time", columnDefinition = "TIMESTAMP")
    private String createTime;

    @Column(name = "`flag`")
    private Integer flag = 0;

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

    public String getLac() {
        return lac;
    }

    public void setLac(String lac) {
        this.lac = lac;
    }

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }

    public String getSignal() {
        return signal;
    }

    public void setSignal(String signal) {
        this.signal = signal;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("lac", lac)
                .append("cellId", cellId)
                .append("mcc", mcc)
                .append("mnc", mnc)
                .append("signal", signal)
                .append("createTime", createTime)
                .append("flag", flag)
                .append("locationUuid", locationUuid)
                .append("deviceId", deviceId).toString();
    }

    public String toStringAutoNavi(){
        return mcc + ","
                + mnc + ","
                + lac + ","
                + cellId + ","
                + signal;
    }
}
