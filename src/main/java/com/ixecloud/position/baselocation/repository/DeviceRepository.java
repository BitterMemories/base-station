package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Integer> {
}
