package com.ga.DroneCoordinatesGenerator.service;

import com.ga.DroneCoordinatesGenerator.exception.AccessDeniedException;
import com.ga.DroneCoordinatesGenerator.exception.InformationNotFoundException;
import com.ga.DroneCoordinatesGenerator.model.History;
import com.ga.DroneCoordinatesGenerator.model.User;
import com.ga.DroneCoordinatesGenerator.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ga.DroneCoordinatesGenerator.service.UserService.getCurrentLoggedInUser;

@Service
public class HistoryService {
    private HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository,
                          UserService userService){
        this.historyRepository = historyRepository;
    }

    public List<History> getHistories(){
        User user = getCurrentLoggedInUser();
        if(user.getRole().equals(User.Role.ADMIN))
            return historyRepository.findAll();
        return historyRepository.findByUserId(user.getId());
    }

    public History getHistory(Long historyId){
        User user = getCurrentLoggedInUser();
        if(user.getRole().equals(User.Role.ADMIN))
            return historyRepository.findById(historyId).orElseThrow(
                    () -> new InformationNotFoundException("No history with id " + historyId + " exists")
            );
        return historyRepository.findByIdAndUserId(historyId, user.getId()).orElseThrow(
                () -> new InformationNotFoundException("No history found for this Id and user")
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public History deleteHistory(Long historyId){
        History history = historyRepository.findById(historyId).orElseThrow(
                () -> new InformationNotFoundException("No history with id " + historyId + " exists")
        );
        historyRepository.delete(history);
        return history;
    }
}
