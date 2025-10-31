package com.pet.project.reservations.availability;

import java.time.LocalDate;

public record CheckAvailabilityResponse(
        String message,
        AvailableStatus status
) {
}
