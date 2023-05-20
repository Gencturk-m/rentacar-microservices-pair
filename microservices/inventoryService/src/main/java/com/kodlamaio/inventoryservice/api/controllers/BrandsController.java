package com.kodlamaio.inventoryservice.api.controllers;

import com.kodlamaio.inventoryservice.business.abstracts.BrandService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateBrandRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateBrandRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateBrandResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllBrandsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetBrandResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateBrandResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/brands")
public class BrandsController {
    private final BrandService brandService;
    public BrandsController(BrandService brandService) {
        this.brandService = brandService;
    }
    @GetMapping()
    public List<GetAllBrandsResponse> findAll(){
        return brandService.getAll();
    }
    @GetMapping("/{id}")
    public GetBrandResponse getById(@PathVariable("id") UUID id){
        return brandService.getById(id);
    }
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public CreateBrandResponse add(@Valid @RequestBody CreateBrandRequest createBrandRequest){
        return brandService.add(createBrandRequest);
    }
    @PutMapping()
    public UpdateBrandResponse update(@RequestBody UpdateBrandRequest updateBrandRequest){
        return brandService.update(updateBrandRequest);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        brandService.delete(id);
    }
}
