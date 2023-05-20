package com.kodlamaio.inventoryservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeleteEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceReturnCarEvent;
import com.kodlamaio.inventoryservice.business.abstracts.CarService;
import com.kodlamaio.inventoryservice.entities.enums.CarState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceConsumer {
    private final CarService carService;

    @KafkaListener(
            topics = "maintenance-created",
            groupId = "inventory-maintenance-create"
    )
    public void consume(MaintenanceCreatedEvent maintenanceCreatedEvent) {
        carService.changeStateByCarId(CarState.MAINTENANCE, maintenanceCreatedEvent.getCarId());
        log.info("Maintenance created event consumed {}", maintenanceCreatedEvent);
    }

    @KafkaListener(
            topics = "maintenance-deleted",
            groupId = "inventory-maintenance-delete"
    )
    public void consume(MaintenanceDeleteEvent maintenanceDeleteEvent) {
        carService.changeStateByCarId(CarState.AVAILABLE, maintenanceDeleteEvent.getCarId());
        log.info("Maintenance deleted event consumed {}", maintenanceDeleteEvent);
    }

    @KafkaListener(
            topics = "maintenance-return-car",
            groupId = "inventory-maintenance-return"
    )
    public void consume(MaintenanceReturnCarEvent maintenanceReturnCarEvent) {
        carService.changeStateByCarId(CarState.AVAILABLE, maintenanceReturnCarEvent.getCarId());
        log.info("Maintenance car return event consumed {}", maintenanceReturnCarEvent);
    }
}
