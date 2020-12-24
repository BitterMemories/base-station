package com.ixecloud.position.baselocation.pojo.mifi.request;

import com.ixecloud.position.baselocation.pojo.BaseEntity;

public class BaseStationEliminateEntity extends BaseEntity {

    //基站编号
    private String cellId;

    private String mnc;

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mnc = mnc;
    }
}
