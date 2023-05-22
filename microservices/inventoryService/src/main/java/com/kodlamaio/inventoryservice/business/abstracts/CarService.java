package com.kodlamaio.inventoryservice.business.abstracts;

import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateCarRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllCarsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetCarResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateCarResponse;
import com.kodlamaio.inventoryservice.entities.enums.CarState;

import java.util.List;
import java.util.UUID;

public interface CarService {
    List<GetAllCarsResponse> getAll(boolean isMaintenanceIncluded);
    GetCarResponse getById(UUID id);
    CreateCarResponse add(CreateCarRequest createCarRequest);
    UpdateCarResponse update(UpdateCarRequest updateCarRequest);
    void delete(UUID id);
    ClientResponse checkIfCarAvailable(UUID id);
    void changeStateByCarId(CarState carState, UUID id);
}
