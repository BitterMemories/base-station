package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer> {

    Manager findManagerByDeviceId(String deviceId);

}
