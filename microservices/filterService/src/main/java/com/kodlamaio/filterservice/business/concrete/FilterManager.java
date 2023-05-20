package com.kodlamaio.filterservice.business.concrete;

import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.filterservice.business.abstracts.FilterService;
import com.kodlamaio.filterservice.business.dto.responses.GetAllFiltersResponse;
import com.kodlamaio.filterservice.business.dto.responses.GetFilterResponse;
import com.kodlamaio.filterservice.entities.Filter;
import com.kodlamaio.filterservice.repository.FilterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilterManager implements FilterService {
    private final FilterRepository filterRepository;
    private final ModelMapperService modelMapperService;

    @Override
    public List<GetAllFiltersResponse> getAll() {
        var filters = filterRepository.findAll();
        var response = filters
                .stream()
                .map(filter -> modelMapperService.forResponse().map(filter, GetAllFiltersResponse.class))
                .collect(Collectors.toList());

        return response;
    }

    @Override
    public GetFilterResponse getById(UUID id) {
        var filter = filterRepository.findById(id);
        var response = modelMapperService.forResponse().map(filter, GetFilterResponse.class);

        return response;
    }

    @Override
    public void add(Filter filter) {
        filterRepository.save(filter);
    }

    @Override
    public void delete(UUID id) {
        filterRepository.deleteById(id);
    }

    @Override
    public void deleteAllByBrandId(UUID brandId) {
        filterRepository.deleteAllByBrandId(brandId);
    }

    @Override
    public void deleteAllByModelId(UUID modelId) {

    }

    @Override
    public void deleteByCarId(UUID carId) {
        filterRepository.deleteByCarId(carId);
    }

    @Override
    public Filter getByCarId(UUID carId) {
        return filterRepository.findByCarId(carId);
    }
}
