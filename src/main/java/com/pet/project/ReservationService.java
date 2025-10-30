package com.pet.project;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {
    private final ReservationRepository repo;

    public Reservation create (Reservation reservation){

        if (!reservation.getEndDate().isAfter(reservation.getStartDate())) {
            log.error("StartDate must be before EndDate");
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }

        if (reservation.getReservationStatus() != null) {
            log.error("Reservation status must be empty");
            throw new IllegalArgumentException("Reservation status must be empty");
        }

        ReservationEntity entity = ReservationMapper.toEntity(reservation);
        entity.setReservationStatus(ReservationStatus.PENDING);

        ReservationEntity save = repo.save(entity);
        Reservation saved = ReservationMapper.toReservation(save);

        log.info("Reservation created: {}", saved.toString());
        return saved;
    }

    public Reservation getReservationById(Long id){
        Optional<ReservationEntity> byId = repo.findById(id);

        if (byId.isPresent()){
            ReservationEntity entity = byId.get();
            return ReservationMapper.toReservation(entity);
        }
        else throw new EntityNotFoundException ("Not found with id = " + id);

    }

    public List<Reservation> findAllReservation(){
        List<ReservationEntity> allEntity = repo.findAll();
        return  allEntity.stream()
                .map(ReservationMapper::toReservation)
                .toList();
    }

    @Transactional
    public void cancelReservationById (Long id){
        Optional<ReservationEntity> byId = repo.findById(id);

        ReservationEntity entity = byId
                .orElseThrow(() ->
                        new EntityNotFoundException("Reservation not found with id = " + id));

        if(entity.getReservationStatus().equals(ReservationStatus.APPROVED)){
            log.error("Can't cancel reservation with id = {}", id);
            throw new IllegalStateException(
                    "Can't cancel reservation. Please contact your personal manager"
            );
        }

        if(entity.getReservationStatus().equals(ReservationStatus.CANCELLED)){
            log.error("Can't cancel reservation with id = {}", id);
            throw new IllegalStateException(
                    "Can't cancel. The reservation was canceled before"
            );
        }

        repo.setStatus(id, ReservationStatus.CANCELLED);

        log.info("Reservation has been removed with id = {}", id);
    }

    public Reservation updateReservation(Long id, Reservation reservation){
        Reservation reservationById = ReservationMapper.toReservation(
                repo.findById(id)
                        .orElseThrow(
                                () -> new EntityNotFoundException("Not found with id = " + id))
        );

        if (!reservation.getEndDate().isAfter(reservation.getStartDate())) {
            log.error("StartDate must be before EndDate");
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }

        if (reservationById.getReservationStatus() != ReservationStatus.PENDING){
            throw new IllegalStateException("Can't modify reservation: status = " + reservationById.getReservationStatus().toString());
        }

        ReservationEntity updatedReservation =
                new ReservationEntity(
                        reservationById.getId(),
                        reservation.getUserId(),
                        reservation.getRoomId(),
                        reservation.getStartDate(),
                        reservation.getEndDate(),
                        ReservationStatus.PENDING
                );

        repo.save(updatedReservation);

        return ReservationMapper.toReservation(updatedReservation);
    }

    private boolean isConflict (Reservation reservation){

        List<Reservation> all = repo.findAll().stream()
                .map(ReservationMapper::toReservation)
                .toList();

        for (Reservation exsistedReservation : all)
        {
            if (exsistedReservation.getId().equals(reservation.getId())){
                continue;
            }
            if (!exsistedReservation.getRoomId().equals(reservation.getRoomId())){
                continue;
            }
            if (!exsistedReservation.getReservationStatus().equals(ReservationStatus.APPROVED)){
                continue;
            }
            if (reservation.getStartDate().isBefore(exsistedReservation.getEndDate())
                    && exsistedReservation.getStartDate().isBefore(reservation.getEndDate())
            ) {
                return true;
            }
        }
        return false;
    }
    
    public Reservation approveReservation (Long id){
        ReservationEntity entity = repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Not found with id = " + id));

        Reservation reservation = ReservationMapper.toReservation(entity);

        if (isConflict(reservation)){
            log.warn("Not approved due to conflict: id = {}", id);
            throw new NotApprovedException("\"Not approved due to conflict: id = " + id);
        }

        ReservationEntity updatedReservation =
                new ReservationEntity(
                        reservation.getId(),
                        reservation.getUserId(),
                        reservation.getRoomId(),
                        reservation.getStartDate(),
                        reservation.getEndDate(),
                        ReservationStatus.APPROVED
                );

        ReservationEntity save = repo.save(updatedReservation);
        return ReservationMapper.toReservation(save);
    }
}
