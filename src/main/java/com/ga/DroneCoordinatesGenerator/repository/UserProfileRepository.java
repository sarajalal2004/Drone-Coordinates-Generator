package com.ga.DroneCoordinatesGenerator.repository;

import com.ga.DroneCoordinatesGenerator.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile,Long> {
}
