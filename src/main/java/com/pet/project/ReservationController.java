package com.pet.project;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<?> createReservation(
            @RequestBody Reservation reservation
    ) {
        try {
            log.info("Called createReservation");
            return ResponseEntity.ok(reservationService.create(reservation));
        }catch (IllegalArgumentException e){
            log.error("Couldn't create a new reservation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(
            @PathVariable("id") Long id
    ) {
        try {
            log.info("Called getReservationById: id = {}", id);
            return ResponseEntity.ok(reservationService.getReservationById(id));
        }catch (EntityNotFoundException e){
            log.error("Can't get with id = {} {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        try {
            log.info("Called deleteById: id = {}", id);
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Reservation has been removed with id = " + id);
        }catch (NoSuchElementException e){
            log.error("Can't delete: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(
            @PathVariable("id") Long id,
            @RequestBody Reservation reservation
    ) {
        try{
            log.info("Called updateById for id = {}", id );
            return ResponseEntity.ok(reservationService.updatereservation(id, reservation));
        }catch (IllegalStateException e){
            log.error("Can't update: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
