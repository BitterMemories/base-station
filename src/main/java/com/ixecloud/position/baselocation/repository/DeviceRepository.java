package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Device findDeviceByDeviceId(String deviceId);

    @Modifying
    @Transactional
    int deleteDeviceByDeviceId(String deviceId);

}
