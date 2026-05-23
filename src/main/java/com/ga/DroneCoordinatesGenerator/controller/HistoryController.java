package com.ga.DroneCoordinatesGenerator.controller;

import com.ga.DroneCoordinatesGenerator.model.History;
import com.ga.DroneCoordinatesGenerator.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/histories")
public class HistoryController {
    private HistoryService historyService;

    @Autowired
    public void setHistoryService(HistoryService historyService){
        this.historyService = historyService;
    }

    @GetMapping
    public List<History> getHistories(){
        return historyService.getHistories();
    }

    @GetMapping(path = "/{historyId}")
    public History getHistory(@PathVariable("historyId") Long historyId){
        return historyService.getHistory(historyId);
    }

    @DeleteMapping(path = "/{historyId}")
    public History deleteHistory(@PathVariable("historyId") Long historyId){
        return historyService.deleteHistory(historyId);
    }
}
