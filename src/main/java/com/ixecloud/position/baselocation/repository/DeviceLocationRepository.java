package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.DeviceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DeviceLocationRepository extends JpaRepository<DeviceLocation, Integer> {

    @Query(value = "select * from device_location as a where id=(select max(b.id) from device_location as b where b.device_id = ?1); ", nativeQuery = true)
    DeviceLocation findDeviceLocationByDeviceId(String deviceId);

    @Transactional
    @Modifying
    int deleteDeviceLocationByDeviceId(String deviceId);
}
