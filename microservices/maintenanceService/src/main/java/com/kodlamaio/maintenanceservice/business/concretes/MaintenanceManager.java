package com.kodlamaio.maintenanceservice.business.concretes;

import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeleteEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceReturnCarEvent;
import com.kodlamaio.commonpackage.kafka.KafkaProducer;
import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.maintenanceservice.business.abstracts.MaintenanceService;
import com.kodlamaio.maintenanceservice.business.dto.requests.create.CreateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.requests.update.UpdateMaintenanceRequest;
import com.kodlamaio.maintenanceservice.business.dto.responses.create.CreateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.get.GetAllMaintenancesResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.get.GetMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.dto.responses.update.UpdateMaintenanceResponse;
import com.kodlamaio.maintenanceservice.business.rules.MaintenanceBusinessRules;
import com.kodlamaio.maintenanceservice.entities.Maintenance;
import com.kodlamaio.maintenanceservice.repository.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class MaintenanceManager implements MaintenanceService {
    private final ModelMapperService mapper;
    private final MaintenanceRepository repository;
    private final MaintenanceBusinessRules rules;
    private final KafkaProducer kafkaProducer;

    @Override
    public List<GetAllMaintenancesResponse> getAll() {
        List<Maintenance> maintenances = repository.findAll();
        List<GetAllMaintenancesResponse> response = maintenances.stream()
                .map(maintenance -> mapper.forResponse().map(maintenance, GetAllMaintenancesResponse.class)).toList();
        return response;
    }

    @Override
    public GetMaintenanceResponse getById(UUID id) {

        rules.checkIfMaintenanceExists(id);

        Maintenance maintenance = repository.findById(id).orElseThrow();
        GetMaintenanceResponse response = mapper.forResponse().map(maintenance,GetMaintenanceResponse.class);
        return response;
    }

    @Override
    public GetMaintenanceResponse returnCarFromMaintenance(UUID carId) {

        rules.checkIfCarIsNotUnderMaintenance(carId);

        Maintenance maintenance = repository.findByCarIdAndIsCompletedIsFalse(carId);
        maintenance.setCompleted(true);
        maintenance.setEndDate(LocalDateTime.now());
        Maintenance endedMaintenance = repository.save(maintenance);
        sendKafkaReturnCarEvent(carId);
        GetMaintenanceResponse response = mapper.forResponse().map(endedMaintenance,GetMaintenanceResponse.class);
        return response;
    }

    @Override
    public CreateMaintenanceResponse create(CreateMaintenanceRequest request) {

        rules.ensureCarIsAvailable(request.getCarId());
        rules.checkIfCarUnderMaintenance(request.getCarId());
        rules.checkIfCarIsRented(request.getCarId());

        Maintenance maintenance = mapper.forRequest().map(request,Maintenance.class);
        maintenance.setId(UUID.randomUUID());
        maintenance.setCompleted(false);
        maintenance.setStartDate(LocalDateTime.now());
        maintenance.setEndDate(null);
        Maintenance createdMaintenance = repository.save(maintenance);
        sendKafkaMaintenanceCreatedEvent(createdMaintenance.getCarId());
        CreateMaintenanceResponse response = mapper.forResponse().map(createdMaintenance,CreateMaintenanceResponse.class);
        return response;
    }

    @Override
    public UpdateMaintenanceResponse update(UUID id, UpdateMaintenanceRequest request) {

        rules.checkIfMaintenanceExists(id);

        Maintenance maintenance=  mapper.forRequest().map(request,Maintenance.class);
        maintenance.setId(id);
        Maintenance updatedMaintenance = repository.save(maintenance);
        UpdateMaintenanceResponse response = mapper.forResponse().map(updatedMaintenance,UpdateMaintenanceResponse.class);
        return response;
    }

    @Override
    public void delete(UUID id) {

        rules.checkIfMaintenanceExists(id);

        sendKafkaMaintenanceDeletedEvent(id);
        repository.deleteById(id);
    }
    private void sendKafkaMaintenanceCreatedEvent(UUID carId){
        kafkaProducer.sendMessage(new MaintenanceCreatedEvent(carId), "maintenance-created");
    }
    private void sendKafkaMaintenanceDeletedEvent(UUID id){
        var carId = repository.findById(id).orElseThrow().getCarId();
        kafkaProducer.sendMessage(new MaintenanceDeleteEvent(carId), "maintenance-deleted");
    }
    private void sendKafkaReturnCarEvent(UUID carId){
        kafkaProducer.sendMessage(new MaintenanceReturnCarEvent(carId), "maintenance-return-car");
    }

}
