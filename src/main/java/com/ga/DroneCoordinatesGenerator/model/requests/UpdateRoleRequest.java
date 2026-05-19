package com.ga.DroneCoordinatesGenerator.model.requests;

import com.ga.DroneCoordinatesGenerator.model.User;
import lombok.Getter;

@Getter
public class UpdateRoleRequest {
    private String email;
    private User.Role role;
}
