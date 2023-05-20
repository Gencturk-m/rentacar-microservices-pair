package com.kodlamaio.inventoryservice.api.controllers;

import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.inventoryservice.business.abstracts.CarService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllCarsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateCarResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarsController {
    private final CarService carService;

    @GetMapping()
    public List<GetAllCarsResponse> getAll(@RequestParam(required = false) boolean isMaintenanceIncluded){
        return carService.getAll(isMaintenanceIncluded);
    }
    @GetMapping("/{id}")
    public GetCarResponse getById(@PathVariable("id") UUID id){
        return carService.getById(id);
    }
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CreateCarResponse add(@Valid @RequestBody CreateCarRequest createCarRequest){
        return carService.add(createCarRequest);
    }
    @PutMapping()
    public UpdateCarResponse update(@RequestBody UpdateCarRequest updateCarRequest){
        return carService.update(updateCarRequest);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        carService.delete(id);
    }

    @GetMapping("/check-car-available/{id}")
    public ClientResponse checkIfCarAvailable(@PathVariable UUID id) {
        return carService.checkIfCarAvailable(id);
    }

    @GetMapping("/api/cars/check-car-rented/{carId}")
    public ClientResponse checkIfCarRented(@PathVariable UUID carId){
        return carService.checkIfCarRented(carId);
    }

    @GetMapping("/api/cars/check-car-under-maintenance/{carId}")
    public ClientResponse checkCarUnderMaintenance(@PathVariable UUID carId){
        return carService.checkIfCarUnderMaintenance(carId);
    }
    @GetMapping("/api/cars/check-car-not-under-maintenance/{carId}")
    public ClientResponse checkCarNotUnderMaintenance(@PathVariable UUID carId){
        return carService.checkIfCarNotUnderMaintenance(carId);
    }

}
