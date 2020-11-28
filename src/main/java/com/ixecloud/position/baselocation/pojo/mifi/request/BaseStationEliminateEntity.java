package com.ixecloud.position.baselocation.pojo.mifi.request;

import com.ixecloud.position.baselocation.pojo.BaseEntity;

public class BaseStationEliminateEntity extends BaseEntity {

    //基站编号
    private String cellId;

    public String getCellId() {
        return cellId;
    }

    public void setCellId(String cellId) {
        this.cellId = cellId;
    }
}
