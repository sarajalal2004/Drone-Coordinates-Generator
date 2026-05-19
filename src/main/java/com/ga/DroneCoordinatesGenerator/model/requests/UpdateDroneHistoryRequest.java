package com.ga.DroneCoordinatesGenerator.model.requests;

import lombok.Getter;

@Getter
public class UpdateDroneHistoryRequest {
    private Long historyId;
    private Long DroneId;
}
