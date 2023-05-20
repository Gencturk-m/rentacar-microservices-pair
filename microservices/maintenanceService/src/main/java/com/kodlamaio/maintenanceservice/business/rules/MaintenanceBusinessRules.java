package com.kodlamaio.maintenanceservice.business.rules;

import com.kodlamaio.commonpackage.utils.constants.Messages;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.maintenanceservice.api.clients.CarClient;
import com.kodlamaio.maintenanceservice.repository.MaintenanceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class MaintenanceBusinessRules {
    private final MaintenanceRepository repository;
    private final CarClient carClient;
    public void checkIfMaintenanceExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new BusinessException(Messages.Maintenance.NotExists);
        }
    }

    public void ensureCarIsAvailable(UUID carId) {
        var response = carClient.checkIfCarAvailable(carId);
        if (!response.isSuccess()) {
            throw new BusinessException(response.getMessage());
        }
    }
    public void checkIfCarIsRented(UUID carId) {
        var response = carClient.checkIfCarIsRented(carId);
        if (!response.isSuccess()){
            throw new BusinessException(Messages.Maintenance.CarIsRented);
        }
    }
    public void checkIfCarIsNotUnderMaintenance(UUID carId) {
        var response = carClient.checkIfCarNotUnderMaintenance(carId);
        if (!response.isSuccess()) {
            throw new BusinessException(Messages.Maintenance.CarNotExists);
        }
    }

    public void checkIfCarUnderMaintenance(UUID carId) {
        var response = carClient.checkIfCarUnderMaintenance(carId);
        if (!response.isSuccess()) {
            throw new BusinessException(Messages.Maintenance.CarExists);
        }
    }
}
