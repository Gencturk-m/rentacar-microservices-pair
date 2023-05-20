package com.kodlamaio.inventoryservice.api.controllers;

import com.kodlamaio.inventoryservice.business.abstracts.ModelService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateModelRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateModelRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateModelResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllModelsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetModelResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateModelResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/models")
public class ModelsController {
    private final ModelService modelService;
    public ModelsController(ModelService modelService) {
        this.modelService = modelService;
    }
    @GetMapping()
    public List<GetAllModelsResponse> findAll(){
        return modelService.getAll();
    }
    @GetMapping("/{id}")
    public GetModelResponse getById(@PathVariable("id") UUID id){
        return modelService.getById(id);
    }
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CreateModelResponse add(@Valid @RequestBody CreateModelRequest createModelRequest){
        return modelService.add(createModelRequest);
    }
    @PutMapping()
    public UpdateModelResponse update(@RequestBody UpdateModelRequest updateModelRequest){
        return modelService.update(updateModelRequest);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        modelService.delete(id);
    }
}
