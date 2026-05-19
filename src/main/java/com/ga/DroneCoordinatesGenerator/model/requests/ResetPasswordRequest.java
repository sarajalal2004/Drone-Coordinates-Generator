package com.ga.DroneCoordinatesGenerator.model.requests;

import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
