package com.ga.DroneCoordinatesGenerator.repository;

import com.ga.DroneCoordinatesGenerator.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DroneRepository extends JpaRepository<Drone,Long> {
}
