package com.pet.project.reservations;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
            @RequestBody @Valid Reservation reservation
    ) {
            log.info("Called createReservation");
            return ResponseEntity.ok(reservationService.create(reservation));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ) {
            log.info("Called getReservationById: id = {}", id);
            return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Reservation>> getAllReservations(
            @RequestParam(name = "roomId", required = false) Long roomId,
            @RequestParam(name = "userId", required = false) Long userId,
            @RequestParam(name = "status", required = false) ReservationStatus status,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ){
        log.info("Called getAllReservations");
        var filter = new ReservationSearchFilter(
                roomId,
                userId,
                status,
                pageSize,
                pageNumber
        );
        return ResponseEntity.ok(reservationService.searchAllByFilter(filter));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelReservationById(
            @PathVariable("id") Long id
    ) {
            log.info("Called cancelReservationById: id = {}", id);
            reservationService.cancelReservationById(id);
            return ResponseEntity.ok("Reservation has been canceled with id = " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateById(
            @PathVariable("id") Long id,
            @RequestBody @Valid Reservation reservation
    ) {
            log.info("Called updateById for id = {}", id );
            return ResponseEntity.ok(reservationService.updateReservation(id, reservation));
    }
}
