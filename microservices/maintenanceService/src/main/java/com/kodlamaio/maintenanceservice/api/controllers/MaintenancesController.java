package com.kodlamaio.maintenanceservice.api.controllers;

import com.kodlamaio.maintenanceservice.business.abstracts.MaintenanceService;
import com.kodlamaio.maintenanceservice.business.dto.requests.CreateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.requests.UpdateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.responses.CreateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.GetAllMaintenancesResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.GetMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.UpdateMaintenanceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/maintenances")
public class MaintenancesController {
    private final MaintenanceService maintenanceService;

    public MaintenancesController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping()
    public List<GetAllMaintenancesResponse> findAll(){
        return maintenanceService.getAll();
    }
    @GetMapping("/{id}")
    public GetMaintenanceResponse getById(@PathVariable("id") UUID id){
        return maintenanceService.getById(id);
    }
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CreateMaintenanceResponse add(@Valid @RequestBody CreateMaintenanceRequest createMaintenanceRequest){
        return maintenanceService.add(createMaintenanceRequest);
    }
    @PutMapping()
    public UpdateMaintenanceResponse update(@RequestBody UpdateMaintenanceRequest updateMaintenanceRequest){
        return maintenanceService.update(updateMaintenanceRequest);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        maintenanceService.delete(id);
    }
    @PutMapping("/return/{id}")
    public GetMaintenanceResponse returnCarFromMaintenance(@PathVariable("id") UUID carId){
        return maintenanceService.returnCarFromMaintenance(carId);
    }
}