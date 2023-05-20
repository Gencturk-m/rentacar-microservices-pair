package com.kodlamaio.maintenanceservice.api.clients;

import com.kodlamaio.commonpackage.utils.dto.ClientResponse;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "inventory-service", fallback = CarClientFallback.class)
public interface CarClient {
    @GetMapping(value = "/api/cars/check-car-available/{carId}")
    @Retry(name = "rental-client-retry")
    ClientResponse checkIfCarAvailable(@PathVariable UUID carId);

    @GetMapping(value = "/api/cars/check-car-rented/{carId}")
    @Retry(name = "rental-client-retry")
    ClientResponse checkIfCarIsRented(@PathVariable UUID carId);

    @GetMapping(value = "/api/cars/check-car-under-maintenance/{carId}")
    @Retry(name = "rental-client-retry")
    ClientResponse checkIfCarUnderMaintenance(@PathVariable UUID carId);

    @GetMapping(value = "/api/cars/check-car-not-under-maintenance/{carId}")
    @Retry(name = "rental-client-retry")
    ClientResponse checkIfCarNotUnderMaintenance(@PathVariable UUID carId);
}
