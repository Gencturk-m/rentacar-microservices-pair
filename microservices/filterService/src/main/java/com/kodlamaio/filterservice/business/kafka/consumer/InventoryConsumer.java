package com.kodlamaio.filterservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.inventory.BrandDeletedEvent;
import com.kodlamaio.commonpackage.events.inventory.CarCreatedEvent;
import com.kodlamaio.commonpackage.events.inventory.CarDeletedEvent;
import com.kodlamaio.commonpackage.utils.mappers.ModelMapperService;
import com.kodlamaio.filterservice.business.abstracts.FilterService;
import com.kodlamaio.filterservice.entities.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryConsumer {
    private final FilterService filterService;
    private final ModelMapperService modelMapperService;

    @KafkaListener(
            topics = "car-created",
            groupId = "car-create"
    )
    public void consume(CarCreatedEvent carCreatedEvent) {
        var filter = modelMapperService.forRequest().map(carCreatedEvent, Filter.class);
        filterService.add(filter);
        log.info("Car created event consumed {}", carCreatedEvent);
    }

    @KafkaListener(
            topics = "car-deleted",
            groupId = "car-delete"
    )
    public void consume(CarDeletedEvent carDeletedEvent) {
        filterService.deleteByCarId(carDeletedEvent.getCarId());
        log.info("Car deleted event consumed {}", carDeletedEvent);
    }


    @KafkaListener(
            topics = "brand-deleted",
            groupId = "brand-delete"
    )
    public void consume(BrandDeletedEvent brandDeletedEvent) {
        filterService.deleteAllByBrandId(brandDeletedEvent.getBrandId());
        log.info("Car deleted event consumed {}", brandDeletedEvent);
    }
}
