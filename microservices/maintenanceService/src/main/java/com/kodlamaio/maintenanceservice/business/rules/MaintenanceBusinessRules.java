package com.kodlamaio.maintenanceservice.business.rules;

import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.maintenanceservice.api.client.CarClient;
import com.kodlamaio.maintenanceservice.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MaintenanceBusinessRules {
    private final MaintenanceRepository maintenanceRepository;
    @Qualifier("com.kodlamaio.maintenanceservice.api.client.CarClient")
    private final CarClient carClient;

    public void checkIfMaintenanceExistsById(UUID id){
        if(!maintenanceRepository.existsById(id)){
            throw new BusinessException("This maintenance info is not registered in the system.");
        }
    }
    public void checkIfCarIsInMaintenance(UUID carId){
        if(maintenanceRepository.existsByCarIdAndIsCompletedIsFalse(carId)){
            throw new BusinessException("This car is in maintenance.");
        }
    }
    public void checkIfCarIsNotInMaintenance(UUID carId) {
        if (!maintenanceRepository.existsByCarIdAndIsCompletedIsFalse(carId)) {
            throw new BusinessException("This car is no in maintenance");
        }
    }
    public void ensureCarIsAvailable(UUID carId){
        var response = carClient.checkIfCarAvailable(carId);
        if (!response.isSuccess()) {
            throw new BusinessException(response.getMessage());
        }
    }

}
