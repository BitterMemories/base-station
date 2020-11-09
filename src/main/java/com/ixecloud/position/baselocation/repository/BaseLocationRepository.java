package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.BaseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseLocationRepository extends JpaRepository<BaseLocation, Integer> {
}
