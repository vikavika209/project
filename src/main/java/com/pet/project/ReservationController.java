package com.pet.project;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @RequestBody Reservation reservation
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
    public List<Reservation> getAllReservations(){
        log.info("Called getAllReservations");
        return reservationService.findAllReservation();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteById(
            @PathVariable("id") Long id
    ) {
            log.info("Called deleteById: id = {}", id);
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Reservation has been removed with id = " + id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateById(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservation
    ) {
            log.info("Called updateById for id = {}", id );
            return ResponseEntity.ok(reservationService.updatereservation(id, reservation));
    }
}
