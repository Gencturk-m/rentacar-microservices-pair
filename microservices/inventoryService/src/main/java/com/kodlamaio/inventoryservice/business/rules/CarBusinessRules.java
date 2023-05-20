package com.kodlamaio.inventoryservice.business.rules;

import com.kodlamaio.commonpackage.utils.constants.Messages;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.inventoryservice.entities.enums.CarState;
import com.kodlamaio.inventoryservice.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CarBusinessRules {
    private final CarRepository repository;

    public void checkIfCarExists(UUID id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("CAR_NOT_EXISTS");
        }
    }
    public void checkCarAvailability(UUID id) {
        var car = repository.findById(id).orElseThrow();
        if (!car.getCarState().equals(CarState.AVAILABLE)) {
            throw new BusinessException("CAR_NOT_AVAILABLE");
        }
    }
    public void checkCarIsRented(UUID id) {
        var car = repository.findById(id).orElseThrow();
        if (car.getCarState().equals(CarState.RENTED)) {
            throw new BusinessException(Messages.Maintenance.CarIsRented);
        }
    }
    public void checkCarUnderMaintenance(UUID id){
        var car = repository.findById(id).orElseThrow();
        if (car.getCarState().equals(CarState.MAINTENANCE)) {
            throw new BusinessException("CAR_UNDER_MAINTENANCE");
        }
    }
    public void checkCarNotUnderMaintenance(UUID id){
        var car = repository.findById(id).orElseThrow();
        if (!car.getCarState().equals(CarState.MAINTENANCE)) {
            throw new BusinessException("CAR_NOT_UNDER_MAINTENANCE");
        }
    }
}