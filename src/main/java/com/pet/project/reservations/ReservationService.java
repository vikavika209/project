package com.pet.project.reservations;

import com.pet.project.reservations.availability.ReservationAvailableService;
import com.pet.project.web.NotApprovedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRepository repo;
    private final ReservationAvailableService availableService;
    private final ReservationMapper mapper;


    public Reservation create (Reservation reservation){

        if (!reservation.getEndDate().isAfter(reservation.getStartDate())) {
            log.error("StartDate must be before EndDate");
            throw new IllegalArgumentException("StartDate must be before EndDate");
        }

        if (reservation.getReservationStatus() != null) {
            log.error("Reservation status must be empty");
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

    public List<Reservation> searchAllByFilter(
            ReservationSearchFilter filter
    ){
        int pageSize = filter.pageSize() != null
                ? filter.pageSize() : 10;

        int pageNumber = filter.pageNumber() != null
                ? filter.pageNumber() : 0;

        var pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNumber);

        List<ReservationEntity> allEntity = repo.searchAllByFilter(
                filter.roomId(),
                filter.userId(),
                filter.status(),
                pageable
        );
        return  allEntity.stream()
                .map(mapper::toReservation)
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
        Reservation reservationById = mapper.toReservation(
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

        return mapper.toReservation(updatedReservation);
    }


    
    public Reservation approveReservation (Long id){
        ReservationEntity entity = repo.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Not found with id = " + id));

        Reservation reservation = mapper.toReservation(entity);

        if (!availableService.isAvailable(
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate()
        )){
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
        return mapper.toReservation(save);
    }
}
