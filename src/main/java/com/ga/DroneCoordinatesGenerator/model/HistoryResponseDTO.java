package com.ga.DroneCoordinatesGenerator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HistoryResponseDTO {
    private Long id;
    private String image;
    private Integer numberOfDrones;
    private Double width;
    private List<DroneHistoryDTO> droneHistories;

    @Setter@Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DroneHistoryDTO {
        private Long droneId;
        private String droneSerial;
        private Double x;
        private Double y;
    }
}