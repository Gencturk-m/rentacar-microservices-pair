package com.kodlamaio.filterservice.api.controllers;

import com.kodlamaio.filterservice.business.abstracts.FilterService;
import com.kodlamaio.filterservice.business.dto.responses.GetAllFiltersResponse;
import com.kodlamaio.filterservice.business.dto.responses.GetFilterResponse;
import com.kodlamaio.filterservice.entities.Filter;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/filters")
public class FiltersController {
    private final FilterService filterService;

    @GetMapping
    public List<GetAllFiltersResponse> getAll() {
        return filterService.getAll();
    }

    @GetMapping("/{id}")
    public GetFilterResponse getByIId(@PathVariable UUID id) {
        return filterService.getById(id);
    }
}
