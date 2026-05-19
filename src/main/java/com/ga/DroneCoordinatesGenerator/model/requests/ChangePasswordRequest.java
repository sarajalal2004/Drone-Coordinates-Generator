package com.ga.DroneCoordinatesGenerator.model.requests;

import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;
}
