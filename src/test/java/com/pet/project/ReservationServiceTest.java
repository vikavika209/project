package com.pet.project;

import com.pet.project.reservations.*;
import com.pet.project.reservations.availability.ReservationAvailableService;
import com.pet.project.web.NotApprovedException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    ReservationRepository repository;

    @Mock
    ReservationMapper mapper;

    @Mock
    ReservationAvailableService  availableService;

    @InjectMocks
    ReservationService service;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Test
    void create() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity = ReservationEntity.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        when(mapper.toEntity(reservation)).thenReturn(reservationEntity);

        when(repository.save(any(ReservationEntity.class))).thenAnswer(inv -> {
            ReservationEntity arg = inv.getArgument(0);
            return arg;
        });

        when(mapper.toReservation(reservationEntity)).thenReturn(reservation);

        ArgumentCaptor<ReservationEntity> captor =
                ArgumentCaptor.forClass(ReservationEntity.class);

        service.create(reservation);

        verify(repository).save(captor.capture());

        ReservationEntity captured  =  captor.getValue();

        Assertions.assertEquals(ReservationStatus.PENDING, captured .getReservationStatus());

    }

    @Test
    void createIllegalArgumentExceptionDueToDate() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("05.01.2025", formatter))
                .endDate(LocalDate.parse("01.01.2025", formatter))
                .build();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(reservation)
        );
    }

    @Test
    void createIllegalArgumentExceptionDueToStatus() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .reservationStatus(ReservationStatus.PENDING)
                .build();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> service.create(reservation)
        );
    }

    @Test
    void getReservationById() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        ReservationEntity entity = new ReservationEntity();
        entity.setRoomId(100L);
        entity.setRoomId(777L);
        entity.setStartDate(LocalDate.parse("01.01.2025", formatter));
        entity.setEndDate((LocalDate.parse("05.01.2025", formatter)));

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(entity));

        Reservation reservationById = service.getReservationById(1L);
        verify(repository).findById(1L);

    }

    @Test
    void updateReservationThrowEntityNotFoundException() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.updateReservation(1L, reservation)
                );
    }

    @Test
    void updateReservationThrowIllegalStateException() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .reservationStatus(ReservationStatus.APPROVED)
                .build();

        ReservationEntity entity = new ReservationEntity();
        entity.setRoomId(100L);
        entity.setRoomId(777L);
        entity.setStartDate(LocalDate.parse("01.01.2025", formatter));
        entity.setEndDate((LocalDate.parse("05.01.2025", formatter)));
        entity.setReservationStatus(ReservationStatus.APPROVED);

        when(mapper.toReservation(any(ReservationEntity.class))).thenReturn(reservation);
        when(repository.findById(any(Long.class))).thenReturn(Optional.of(entity));

        Assertions.assertThrows(IllegalStateException.class,
                () -> service.updateReservation(1L, reservation)
        );

    }

    @Test
    void updateReservation() {
        Reservation beforeUpdating = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .reservationStatus(ReservationStatus.PENDING)
                .build();

        ReservationEntity beforeUpdatingEntity = ReservationEntity.builder()
                .id(1L)
                .reservationStatus(ReservationStatus.PENDING)
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        Reservation afterUpdating = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("15.01.2025", formatter))
                .build();

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(beforeUpdatingEntity));

        when(mapper.toReservation(any(ReservationEntity.class))).thenReturn(beforeUpdating);

        service.updateReservation(1L, afterUpdating);

        ArgumentCaptor<ReservationEntity> captor =
                ArgumentCaptor.forClass(ReservationEntity.class);

        verify(repository).save(captor.capture());

        ReservationEntity captured = captor.getValue();

        Assertions.assertEquals(LocalDate.parse("15.01.2025", formatter), captured.getEndDate());
    }

    @Test
    void approveReservationThrowEntityNotFoundException() {
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.approveReservation(1L)
        );
    }

    @Test
    void approveReservationThrowNotApprovedException() {

        Reservation reservation1 = Reservation.builder()
                .userId(100L)
                .roomId(1L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        when(mapper.toEntity(any(Reservation.class))).thenReturn(new ReservationEntity());

        ReservationEntity reservationEntity1 = mapper.toEntity(reservation1);
        reservationEntity1.setId(1L);
        reservationEntity1.setUserId(100L);
        reservationEntity1.setRoomId(1L);
        reservationEntity1.setStartDate(LocalDate.parse("01.01.2025", formatter));
        reservationEntity1.setEndDate(LocalDate.parse("05.01.2025", formatter));
        reservationEntity1.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation2 = Reservation.builder()
                .userId(200L)
                .roomId(2L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity2 = mapper.toEntity(reservation2);
        reservationEntity2.setId(2L);
        reservationEntity2.setUserId(200L);
        reservationEntity2.setRoomId(2L);
        reservationEntity2.setStartDate(LocalDate.parse("01.01.2025", formatter));
        reservationEntity2.setEndDate(LocalDate.parse("05.01.2025", formatter));
        reservationEntity2.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation3 = Reservation.builder()
                .userId(300L)
                .roomId(1L)
                .startDate(LocalDate.parse("03.01.2025", formatter))
                .endDate(LocalDate.parse("10.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity3 = mapper.toEntity(reservation3);
        reservationEntity3.setId(3L);
        reservationEntity3.setUserId(300L);
        reservationEntity3.setRoomId(1L);
        reservationEntity3.setStartDate(LocalDate.parse("03.01.2025", formatter));
        reservationEntity3.setEndDate(LocalDate.parse("10.01.2025", formatter));
        reservationEntity3.setReservationStatus(ReservationStatus.PENDING);

        List<ReservationEntity> allEntity = List.of(reservationEntity1, reservationEntity2, reservationEntity3);

        when(repository.findById(3L)).thenReturn(Optional.of(reservationEntity3));
        when(mapper.toReservation(reservationEntity3)).thenReturn(reservation3);
        when(availableService.isAvailable(
                1L,
                LocalDate.parse("03.01.2025", formatter),
                LocalDate.parse("10.01.2025", formatter)))
                .thenReturn(false);

        Assertions.assertThrows(NotApprovedException.class,
                () -> service.approveReservation(3L)
        );
    }

    @Test
    void approveReservation() {

        Reservation reservation1 = Reservation.builder()
                .userId(100L)
                .roomId(1L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        when(mapper.toEntity(any(Reservation.class))).thenReturn(new ReservationEntity());

        ReservationEntity reservationEntity1 = mapper.toEntity(reservation1);
        reservationEntity1.setId(1L);
        reservationEntity1.setUserId(100L);
        reservationEntity1.setRoomId(1L);
        reservationEntity1.setStartDate(LocalDate.parse("01.01.2025", formatter));
        reservationEntity1.setEndDate(LocalDate.parse("05.01.2025", formatter));
        reservationEntity1.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation2 = Reservation.builder()
                .userId(200L)
                .roomId(2L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity2 = mapper.toEntity(reservation2);
        reservationEntity2.setId(2L);
        reservationEntity2.setUserId(200L);
        reservationEntity2.setRoomId(2L);
        reservationEntity2.setStartDate(LocalDate.parse("01.01.2025", formatter));
        reservationEntity2.setEndDate(LocalDate.parse("05.01.2025", formatter));
        reservationEntity2.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation3 = Reservation.builder()
                .userId(300L)
                .roomId(1L)
                .startDate(LocalDate.parse("03.02.2025", formatter))
                .endDate(LocalDate.parse("10.03.2025", formatter))
                .build();

        ReservationEntity reservationEntity3 = mapper.toEntity(reservation3);
        reservationEntity3.setId(3L);
        reservationEntity3.setUserId(300L);
        reservationEntity3.setRoomId(1L);
        reservationEntity3.setStartDate(LocalDate.parse("03.02.2025", formatter));
        reservationEntity3.setEndDate(LocalDate.parse("10.03.2025", formatter));
        reservationEntity3.setReservationStatus(ReservationStatus.PENDING);

        List<ReservationEntity> allEntity = List.of(reservationEntity1, reservationEntity2, reservationEntity3);

        when(repository.findById(3L)).thenReturn(Optional.of(reservationEntity3));

        when(mapper.toReservation(reservationEntity3)).thenReturn(reservation3);

        when(availableService.isAvailable(
                1L,
                LocalDate.parse("03.02.2025", formatter),
                LocalDate.parse("10.03.2025", formatter)))
                .thenReturn(Boolean.TRUE);

        service.approveReservation(3L);

        ArgumentCaptor<ReservationEntity> captor = ArgumentCaptor.forClass(ReservationEntity.class);

        verify(repository).save(captor.capture());

        ReservationEntity captured = captor.getValue();

        Assertions.assertEquals(ReservationStatus.APPROVED, captured.getReservationStatus());

    }

    @Test
    void createReservationThrowIllegalArgumentException() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(1L)
                .startDate(LocalDate.parse("15.01.2025", formatter))
                .endDate(LocalDate.parse("10.01.2025", formatter))
                .build();

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.create(reservation)
                );

    }
}