package com.kodlamaio.filterservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeleteEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceReturnCarEvent;
import com.kodlamaio.filterservice.business.abstracts.FilterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceConsumer {
    private final FilterService filterService;

    @KafkaListener(
            topics = "maintenance-created",
            groupId = "filter-maintenance-create"
    )
    public void consume(MaintenanceCreatedEvent maintenanceCreatedEvent) {
        var filter = filterService.getByCarId(maintenanceCreatedEvent.getCarId());
        filter.setState("MAINTENANCE");
        filterService.add(filter);
        log.info("Maintenance created event consumed {}", maintenanceCreatedEvent);
    }

    @KafkaListener(
            topics = "maintenance-deleted",
            groupId = "filter-maintenance-delete"
    )
    public void consume(MaintenanceDeleteEvent maintenanceDeleteEvent) {
        var filter = filterService.getByCarId(maintenanceDeleteEvent.getCarId());
        filter.setState("AVAILABLE");
        filterService.add(filter);
        log.info("Maintenance deleted event consumed {}", maintenanceDeleteEvent);
    }

    @KafkaListener(
            topics = "maintenance-return-car",
            groupId = "filter-maintenance-return"
    )
    public void consume(MaintenanceReturnCarEvent maintenanceReturnCarEvent) {
        var filter = filterService.getByCarId(maintenanceReturnCarEvent.getCarId());
        filter.setState("AVAILABLE");
        filterService.add(filter);
        log.info("Maintenance car return event consumed {}", maintenanceReturnCarEvent);
    }
}
