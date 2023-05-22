package com.kodlamaio.filterservice.business.kafka.consumer;

import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCompletedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceCreatedEvent;
import com.kodlamaio.commonpackage.events.maintenance.MaintenanceDeletedEvent;
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
    public void consume(MaintenanceDeletedEvent maintenanceDeletedEvent) {
        var filter = filterService.getByCarId(maintenanceDeletedEvent.getCarId());
        filter.setState("AVAILABLE");
        filterService.add(filter);
        log.info("Maintenance deleted event consumed {}", maintenanceDeletedEvent);
    }

    @KafkaListener(
            topics = "maintenance-completed",
            groupId = "filter-maintenance-complete"
    )
    public void consume(MaintenanceCompletedEvent maintenanceCompletedEvent) {
        var filter = filterService.getByCarId(maintenanceCompletedEvent.getCarId());
        filter.setState("AVAILABLE");
        filterService.add(filter);
        log.info("Maintenance completed event consumed {}", maintenanceCompletedEvent);
    }
}
