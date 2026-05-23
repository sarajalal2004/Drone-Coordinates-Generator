package com.ga.DroneCoordinatesGenerator.model.requests;

import lombok.Getter;

@Getter
public class RentDroneRequest {
    private Long renterUserId;
    private Long droneId;
}
