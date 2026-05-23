package com.ga.DroneCoordinatesGenerator.repository;

import com.ga.DroneCoordinatesGenerator.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DroneRepository extends JpaRepository<Drone,Long> {
    Boolean existsBySerial(String serial);
    Optional<Drone> findBySerial(String serial);
}
