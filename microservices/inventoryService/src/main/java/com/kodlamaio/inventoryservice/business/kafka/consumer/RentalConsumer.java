package com.kodlamaio.inventoryservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.rental.RentalCreatedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalDeletedEvent;
import com.kodlamaio.inventoryservice.business.abstracts.CarService;
import com.kodlamaio.inventoryservice.entities.enums.CarState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalConsumer {
    private final CarService carService;

    @KafkaListener(
            topics = "rental-created",
            groupId = "inventory-rental-create"
    )
    public void consume(RentalCreatedEvent rentalCreatedEvent) {
        carService.changeStateByCarId(CarState.RENTED, rentalCreatedEvent.getCarId());
        log.info("Rental created event consumed {}", rentalCreatedEvent);
    }

    @KafkaListener(
            topics = "rental-deleted",
            groupId = "inventory-rental-delete"
    )
    public void consume(RentalDeletedEvent rentalDeletedEvent) {
        carService.changeStateByCarId(CarState.AVAILABLE, rentalDeletedEvent.getCarId());
        log.info("Rental deleted event consumed {}", rentalDeletedEvent);
    }
}
