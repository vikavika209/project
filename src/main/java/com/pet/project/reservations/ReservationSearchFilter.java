package com.pet.project.reservations;

import org.springframework.web.bind.annotation.RequestParam;

public record ReservationSearchFilter(
        Long roomId,
        Long userId,
        ReservationStatus status,
        Integer pageSize,
        Integer pageNumber
){
}
