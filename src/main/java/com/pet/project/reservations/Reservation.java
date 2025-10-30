package com.pet.project.reservations;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class Reservation {
    @Null
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private Long roomId;

    @FutureOrPresent
    @NotNull
    private LocalDate startDate;

    @FutureOrPresent
    @NotNull
    private LocalDate endDate;

    private ReservationStatus reservationStatus;
}
