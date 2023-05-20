package com.kodlamaio.filterservice.business.kafka.consumer;


import com.kodlamaio.commonpackage.events.rental.RentalCreatedEvent;
import com.kodlamaio.commonpackage.events.rental.RentalDeletedEvent;
import com.kodlamaio.filterservice.business.abstracts.FilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RentalConsumer {
    private final FilterService filterService;

    @KafkaListener(
            topics = "rental-created",
            groupId = "filter-rental-create"
    )
    public void consume(RentalCreatedEvent rentalCreatedEvent) {
        var filter = filterService.getByCarId(rentalCreatedEvent.getCarId());
        filter.setState("RENTED");
        filterService.add(filter);
        log.info("Rental created event consumed {}", rentalCreatedEvent);
    }
    @KafkaListener(
            topics = "rental-deleted",
            groupId = "filter-rental-delete"
    )
    public void consume(RentalDeletedEvent rentalDeletedEvent) {
        var filter = filterService.getByCarId(rentalDeletedEvent.getCarId());
        filter.setState("AVAILABLE");
        filterService.add(filter);
        log.info("Rental created event consumed {}", rentalDeletedEvent);
    }
}