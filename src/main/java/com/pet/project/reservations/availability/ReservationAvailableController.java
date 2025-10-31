package com.pet.project.reservations.availability;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservation/availability")
@Slf4j
@RequiredArgsConstructor
public class ReservationAvailableController {
    private final ReservationAvailableService service;

    @PostMapping("/check")
    public ResponseEntity<CheckAvailabilityResponse> checkAvailability (
            @Valid @RequestBody CheckAvailabilityRequest request
    ) {
        boolean available = service.isAvailable(
                request.roomId(),
                request.startDate(),
                request.endDate()
        );

        String message = available
                ? "Reservation is available"
                : "Reservation is not available";

        AvailableStatus status = available
                ? AvailableStatus.AVAILABLE
                : AvailableStatus.RESERVED;

        return ResponseEntity.ok(
                new CheckAvailabilityResponse(
                        message,
                        status
                )
        );
    }
}
