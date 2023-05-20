package com.kodlamaio.maintenanceservice.business.concrete;

import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCompletedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeletedEvent;
import com.kodlamaio.commonpackage.kafka.KafkaProducer;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.maintenanceservice.business.abstracts.MaintenanceService;
import com.kodlamaio.maintenanceservice.business.dto.requests.CreateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.requests.UpdateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.responses.CreateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.GetAllMaintenancesResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.GetMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.UpdateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.rules.MaintenanceBusinessRules;
import com.kodlamaio.maintenanceservice.entities.Maintenance;
import com.kodlamaio.maintenanceservice.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaintenanceManager implements MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final ModelMapper modelMapper;
    private final MaintenanceBusinessRules maintenanceBusinessRules;
    private final KafkaProducer kafkaProducer;
    @Override
    public List<GetAllMaintenancesResponse> getAll() {
        List<Maintenance> maintenances = this.maintenanceRepository.findAll();
        List<GetAllMaintenancesResponse> getAllMaintenancesResponses = maintenances
                .stream()
                .map(maintenance -> modelMapper.map(maintenance, GetAllMaintenancesResponse.class))
                .collect(Collectors.toList());

        return getAllMaintenancesResponses;
    }

    @Override
    public GetMaintenanceResponse getById(UUID id) {
        maintenanceBusinessRules.checkIfMaintenanceExistsById(id);
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow();
        GetMaintenanceResponse response = modelMapper.map(maintenance, GetMaintenanceResponse.class);

        return response;
    }

    @Override
    public CreateMaintenanceResponse add(CreateMaintenanceRequest createMaintenanceRequest) {
        maintenanceBusinessRules.checkIfCarIsInMaintenance(createMaintenanceRequest.getCarId());
        maintenanceBusinessRules.ensureCarIsAvailable(createMaintenanceRequest.getCarId());

        Maintenance maintenance = modelMapper.map(createMaintenanceRequest, Maintenance.class);
        maintenance.setId(UUID.randomUUID());
        maintenance.setCompleted(false);
        maintenance.setStartDate(LocalDate.now());
        maintenance.setEndDate(LocalDate.now());
        kafkaProducer.sendMessage
                (new MaintenanceCreatedEvent(maintenance.getCarId()),"maintenance-created");
       //TODO kafkaproducer carService.changeCarState(createMaintenanceRequest.getCarId(), CarState.MAINTENANCE);
        this.maintenanceRepository.save(maintenance);
        CreateMaintenanceResponse createMaintenanceResponse =
                modelMapper.map(maintenance, CreateMaintenanceResponse.class);
        return createMaintenanceResponse;
    }

    @Override
    public UpdateMaintenanceResponse update(UpdateMaintenanceRequest updateMaintenanceRequest) {
        maintenanceBusinessRules.checkIfMaintenanceExistsById(updateMaintenanceRequest.getId());
        Maintenance maintenance = modelMapper.map(updateMaintenanceRequest, Maintenance.class);
        this.maintenanceRepository.save(maintenance);

        UpdateMaintenanceResponse updateMaintenanceResponse =
                modelMapper.map(maintenance, UpdateMaintenanceResponse.class);
        return updateMaintenanceResponse;
    }

    @Override
    public void delete(UUID id) {
        maintenanceBusinessRules.checkIfMaintenanceExistsById(id);
        makeCarAvailableIfIsCompletedFalse(id);
        this.maintenanceRepository.deleteById(id);
    }

    @Override
    public GetMaintenanceResponse returnCarFromMaintenance(UUID carId) {
        maintenanceBusinessRules.checkIfCarIsNotInMaintenance(carId);
        Maintenance maintenance = maintenanceRepository.findByCarIdAndIsCompletedIsFalse(carId);
        maintenance.setCompleted(true);
        maintenance.setEndDate(LocalDate.now());
        maintenanceRepository.save(maintenance); // Update
        //TODO:kafkaproducer
        kafkaProducer.sendMessage(new MaintenanceCompletedEvent(carId),"maintenance-completed");
        //carService.changeState(carId, State.AVAILABLE);
        GetMaintenanceResponse response = modelMapper.map(maintenance, GetMaintenanceResponse.class);

        return response;
    }

    private void makeCarAvailableIfIsCompletedFalse(UUID id) {
        UUID carId = maintenanceRepository.findById(id).get().getCarId();
        if (maintenanceRepository.existsByCarIdAndIsCompletedIsFalse(carId)) {
            kafkaProducer.sendMessage(new MaintenanceDeletedEvent(carId), "maintenance-deleted");
           //TODO: kafkaproducer carService.changeCarState(carId, CarState.AVAILABLE);
        }
    }

}
