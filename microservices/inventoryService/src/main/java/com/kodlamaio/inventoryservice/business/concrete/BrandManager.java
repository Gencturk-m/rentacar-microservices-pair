package com.kodlamaio.inventoryservice.business.concrete;

import com.kodlamaio.commonpackage.events.inventory.BrandDeletedEvent;
import com.kodlamaio.commonpackage.kafka.KafkaProducer;
import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.inventoryservice.business.abstracts.BrandService;
import com.kodlamaio.inventoryservice.business.dto.requests.create.CreateBrandRequest;
import com.kodlamaio.inventoryservice.business.dto.requests.update.UpdateBrandRequest;
import com.kodlamaio.inventoryservice.business.dto.responses.create.CreateBrandResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetAllBrandsResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.get.GetBrandResponse;
import com.kodlamaio.inventoryservice.business.dto.responses.update.UpdateBrandResponse;
import com.kodlamaio.inventoryservice.business.rules.BrandBusinessRules;
import com.kodlamaio.inventoryservice.entities.Brand;
import com.kodlamaio.inventoryservice.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BrandManager implements BrandService {
    private final BrandRepository brandRepository;
    private final ModelMapperService modelMapperService;
    private final BrandBusinessRules brandBusinessRules;
    private final KafkaProducer kafkaProducer;

    @Override
    public List<GetAllBrandsResponse> getAll() {
        var brands = brandRepository.findAll();
        var response = brands
                .stream()
                .map(brand -> modelMapperService.forResponse().map(brand, GetAllBrandsResponse.class))
                .collect(Collectors.toList());

        return response;
    }
    @Override
    public GetBrandResponse getById(UUID id) {
        brandBusinessRules.checkIfBrandExists(id);
        var brand = brandRepository.findById(id).orElseThrow();
        var response = modelMapperService.forResponse().map(brand, GetBrandResponse.class);

        return response;
    }

    @Override
    public CreateBrandResponse add(CreateBrandRequest createBrandRequest) {
        var brand = modelMapperService.forRequest().map(createBrandRequest, Brand.class);
        brand.setId(null);
        brandRepository.save(brand);
        var response = modelMapperService.forResponse().map(brand, CreateBrandResponse.class);

        return response;
    }

    @Override
    public UpdateBrandResponse update(UpdateBrandRequest updateBrandRequest) {
        var brand = modelMapperService.forRequest().map(updateBrandRequest, Brand.class);
        brandRepository.save(brand);
        var response = modelMapperService.forResponse().map(brand, UpdateBrandResponse.class);

        return response;
    }

    @Override
    public void delete(UUID id) {
        brandRepository.deleteById(id);
        sendKafkaBrandDeletedEvent(id);
    }
    private void sendKafkaBrandDeletedEvent(UUID id) {
        kafkaProducer.sendMessage(new BrandDeletedEvent(id), "brand-deleted");
    }
}
