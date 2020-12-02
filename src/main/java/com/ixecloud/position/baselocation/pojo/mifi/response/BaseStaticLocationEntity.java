package com.ixecloud.position.baselocation.pojo.mifi.response;


import com.ixecloud.position.baselocation.domain.BaseLocation;

import java.util.List;

public class BaseStaticLocationEntity extends AutoNaviEntity.LocationInfo {

    private List<BaseLocation> baseLocationList;

    public List<BaseLocation> getBaseLocationList() {
        return baseLocationList;
    }

    public void setBaseLocationList(List<BaseLocation> baseLocationList) {
        this.baseLocationList = baseLocationList;
    }


}
