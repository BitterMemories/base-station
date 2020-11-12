package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.DeviceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeviceLocationRepository extends JpaRepository<DeviceLocation, Integer> {

    DeviceLocation findDeviceLocationByDeviceId(String deviceId);

    @Transactional
    @Modifying
    int deleteDeviceLocationByDeviceId(String deviceId);
}
