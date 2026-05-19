package com.ga.DroneCoordinatesGenerator.repository;

import com.ga.DroneCoordinatesGenerator.model.DroneHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneHistoryRepository extends JpaRepository<DroneHistory, Long> {
}
