package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.BaseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface BaseLocationRepository extends JpaRepository<BaseLocation, Integer> {

    @Modifying
    @Transactional
    int deleteBaseLocationByDeviceId(String deviceId);

    List<BaseLocation> findBaseLocationByDeviceId(String deviceId);

}
