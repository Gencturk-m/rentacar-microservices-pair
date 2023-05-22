package com.kodlamaio.inventoryservice.business.concrete;

import com.kodlamaio.commonpackage.events.inventory.CarCreatedEvent;
import com.kodlamaio.commonpackage.events.inventory.CarDeletedEvent;
import com.kodlamaio.commonpackage.kafka.KafkaProducer;
import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.commonpackage.utils.exceptions.BusinessException;
import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.inventoryservice.business.abstracts.CarService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllCarsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateCarResponse;
import com.kodlamaio.inventoryservice.business.rules.CarBusinessRules;
import com.kodlamaio.inventoryservice.entities.Car;
import com.kodlamaio.inventoryservice.entities.enums.CarState;
import com.kodlamaio.inventoryservice.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarManager implements CarService {
    private final CarRepository carRepository;
    private final ModelMapperService modelMapperService;
    private final KafkaProducer kafkaProducer;
    private final CarBusinessRules carBusinessRules;

    @Override
    public List<GetAllCarsResponse> getAll(boolean isMaintenanceIncluded) {
        var cars = carRepository.findAll();
        var response = cars
                .stream()
                .map(car -> modelMapperService.forResponse().map(car, GetAllCarsResponse.class))
                .collect(Collectors.toList());

        return response;
    }
    @Override
    public GetCarResponse getById(UUID id) {
        var car = carRepository.findById(id).orElseThrow();
        var response = modelMapperService.forResponse().map(car, GetCarResponse.class);

        return response;
    }
    @Override
    public CreateCarResponse add(CreateCarRequest createCarRequest) {
        var car = modelMapperService.forRequest().map(createCarRequest, Car.class);
        car.setId(null);
        car.setCarState(CarState.AVAILABLE);
        var createdCar = carRepository.save(car);

        sendKafkaCarCreatedEvent(createdCar);

        var response = modelMapperService.forResponse().map(car, CreateCarResponse.class);

        return response;
    }
    @Override
    public UpdateCarResponse update(UpdateCarRequest updateCarRequest) {
        var car = modelMapperService.forRequest().map(updateCarRequest, Car.class);
        carRepository.save(car);
        var response = modelMapperService.forResponse().map(car, UpdateCarResponse.class);

        return response;
    }
    @Override
    public void delete(UUID id) {
        carRepository.deleteById(id);
        sendKafkaCarDeletedEvent(id);
    }

    @Override
    public ClientResponse checkIfCarAvailable(UUID id) {
        var response = new ClientResponse();
        validateCarAvailability(id,response);
        return response;
    }

    @Override
    public void changeStateByCarId(CarState carState, UUID id) {
        carRepository.changeStateByCarId(carState,id);
    }

    private void sendKafkaCarCreatedEvent(Car createdCar) {
        var event = modelMapperService.forResponse().map(createdCar, CarCreatedEvent.class);
        kafkaProducer.sendMessage(event,"car-created");
    }
    private void sendKafkaCarDeletedEvent(UUID id) {
        kafkaProducer.sendMessage(new CarDeletedEvent(id), "car-deleted");
    }
    private void validateCarAvailability(UUID id, ClientResponse response) {
        try {
            carBusinessRules.checkIfCarExists(id);
            carBusinessRules.checkCarAvailability(id);
            response.setSuccess(true);
        } catch (BusinessException exception) {
            response.setSuccess(false);
            response.setMessage(exception.getMessage());
        }
    }
}
