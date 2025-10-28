package com.pet.project;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository repo;
    private final ReservationMapper mapper;

    public Reservation create (Reservation reservation){

        if (reservation.getId() != null) {
            throw new IllegalArgumentException("Id must be empty");
        }

        if (reservation.getReservationStatus() != null) {
            throw new IllegalArgumentException("Reservation status must be empty");
        }

        ReservationEntity entity = mapper.toEntity(reservation);
        entity.setReservationStatus(ReservationStatus.PENDING);

        ReservationEntity save = repo.save(entity);
        Reservation saved = mapper.toReservation(save);

        log.info("Reservation created: {}", saved.toString());
        return saved;

    }

    public Reservation getReservationById(Long id){
        Optional<ReservationEntity> byId = repo.findById(id);

        if (byId.isPresent()){
            ReservationEntity entity = byId.get();
            return mapper.toReservation(entity);
        }
        else throw new EntityNotFoundException ("Not found with id = " + id);

    }

    public List<Reservation> findAllReservation(){
        List<ReservationEntity> allEntity = repo.findAll();
        return  allEntity.stream()
                .map(ReservationMapper::toReservation)
                .toList();
    }

    public void deleteReservation(Long id){
        Optional<ReservationEntity> byId = repo.findById(id);
        ReservationEntity entity = byId
                .orElseThrow(() ->
                        new NoSuchElementException("Reservation not found with id = " + id));

        log.info("Reservation has been removed with id = {}", id);
        repo.delete(entity);
    }

    public Reservation updatereservation (Long id, Reservation reservation){
        Reservation reservationById = getReservationById(id);

        if (reservationById.getReservationStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Can't modify reservation: status = " + reservationById.getReservationStatus().toString());
        }

        reservationById.setUserId(reservation.getUserId());
        reservationById.setRoomId(reservation.getRoomId());
        reservationById.setStartDate(reservation.getStartDate());
        reservationById.setEndDate(reservation.getEndDate());

        ReservationEntity entity = mapper.toEntity(reservationById);
        ReservationEntity save = repo.save(entity);

        return mapper.toReservation(save);
    }
}
