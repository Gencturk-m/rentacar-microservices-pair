package com.kodlamaio.inventoryservice.business.concrete;

import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.inventoryservice.business.abstracts.ModelService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateModelRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateModelRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateModelResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllModelsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetModelResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateModelResponse;
import com.kodlamaio.inventoryservice.entities.Model;
import com.kodlamaio.inventoryservice.repository.ModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ModelManager implements ModelService {
    private final ModelRepository modelRepository;
    private final ModelMapperService modelMapperService;

    public ModelManager(ModelRepository modelRepository,
                        ModelMapperService modelMapperService) {
        this.modelRepository = modelRepository;
        this.modelMapperService = modelMapperService;
    }
    @Override
    public List<GetAllModelsResponse> getAll() {
        var models = modelRepository.findAll();
        var response = models
                .stream()
                .map(model -> modelMapperService.forResponse().map(model, GetAllModelsResponse.class))
                .collect(Collectors.toList());

        return response;
    }
    @Override
    public GetModelResponse getById(UUID id) {
        var model = modelRepository.findById(id).orElseThrow();
        var response = modelMapperService.forResponse().map(model, GetModelResponse.class);

        return response;
    }
    @Override
    public CreateModelResponse add(CreateModelRequest createModelRequest) {
        var model = modelMapperService.forRequest().map(createModelRequest, Model.class);
        model.setId(null);
        modelRepository.save(model);
        var response = modelMapperService.forResponse().map(model, CreateModelResponse.class);

        return response;
    }
    @Override
    public UpdateModelResponse update(UpdateModelRequest updateModelRequest) {
        var model = modelMapperService.forRequest().map(updateModelRequest, Model.class);
        modelRepository.save(model);
        var response = modelMapperService.forResponse().map(model, UpdateModelResponse.class);

        return response;
    }
    @Override
    public void delete(UUID id) {
        modelRepository.deleteById(id);
    }
}
