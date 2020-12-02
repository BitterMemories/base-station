package com.ixecloud.position.baselocation.repository;

import com.ixecloud.position.baselocation.domain.BaseLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BaseLocationRepository extends JpaRepository<BaseLocation, Integer> {

    @Modifying
    @Transactional
    int deleteBaseLocationByDeviceId(String deviceId);

    @Query(value = "SELECT * FROM base_location WHERE location_uuid=(select location_uuid from base_location WHERE device_id=?1 ORDER BY create_time desc LIMIT 1);", nativeQuery = true)
    List<BaseLocation> findBaseLocationsByDeviceIdOrderBySignalAsc(String deviceId);

    List<BaseLocation> findBaseLocationsByDeviceIdAndCellId(String deviceId, String cellId);

    List<BaseLocation> findBaseLocationsByDeviceIdAndFlag(String deviceId, Integer flag);

    @Modifying
    @Query(value = "update base_location set flag = 0 where device_id = ?1 and flag = 1", nativeQuery = true)
    int updateBaseLocationsResetFlag(String deviceId);

    @Modifying
    @Query(value = "update base_location set flag = 0 where device_id = ?1 and flag = 2 and cell_id = ?2", nativeQuery = true)
    int updateBaseLocationsFlagByCellId(String deviceId, String cellId);

}
