package com.ixecloud.position.baselocation.pojo.mifi.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class BaseStation {

    private String id;

    private List<BaseStationInfo> data;

    public static class BaseStationInfo{

        //位置区域码
        private String lac;

        //基站小区编号
        private String cellid;

        //移动用户所属国家代码
        private String mcc;

        //移动网号（中国移动：0、中国联通：1）
        private String mnc;

        //网络信号强度（单位dbm）
        private String dbm;

        private Integer flag = 0;

        public String getLac() {
            return lac;
        }

        public void setLac(String lac) {
            this.lac = lac;
        }

        public String getCellid() {
            return cellid;
        }

        public void setCellid(String cellid) {
            this.cellid = cellid;
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

        public String getDbm() {
            return dbm;
        }

        public void setDbm(String dbm) {
            this.dbm = dbm;
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
                    .append("lac", lac)
                    .append("cellid", cellid)
                    .append("mcc", mcc)
                    .append("mnc", mnc)
                    .append("flag", flag)
                    .append("dbm", dbm).toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<BaseStationInfo> getData() {
        if(ObjectUtils.isNotEmpty(data)){
            data = new ArrayList<>(new HashSet<>(data));
        }
        return data;
    }

    public void setData(List<BaseStationInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("data", data).toString();
    }
}
