package com.ixecloud.position.baselocation.pojo.mifi.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BaseStation {

    @JsonProperty(value="id")
    private String deviceId;

    private List<BaseStationInfo> baseStationInfoList;

    static class BaseStationInfo{

        //位置区域码
        private String lac;

        //基站小区编号
        @JsonProperty(value="cellid")
        private String cellId;

        //移动用户所属国家代码
        private String mcc;

        //移动网号（中国移动：0、中国联通：1）
        private String mnc;

        //网络信号强度（单位dbm）
        @JsonProperty(value="db")
        private String signal;

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
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public List<BaseStationInfo> getBaseStationInfoList() {
        return baseStationInfoList;
    }

    public void setBaseStationInfoList(List<BaseStationInfo> baseStationInfoList) {
        this.baseStationInfoList = baseStationInfoList;
    }
}
