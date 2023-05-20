package com.kodlamaio.maintenanceservice.business.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetMaintenanceResponse {

    private UUID id;
    private UUID carId;
    private String description;
    private boolean isCompleted;
    private LocalDate startDate;
    private LocalDate endDate;
}
