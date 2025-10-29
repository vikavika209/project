package com.pet.project;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    ReservationRepository repository;

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

        when(repository.save(any(ReservationEntity.class))).thenAnswer(inv -> {
            ReservationEntity arg = inv.getArgument(0);
            return arg;
        });

        Reservation reservation1 = service.create(reservation);

        Assertions.assertEquals(ReservationStatus.PENDING, reservation1.getReservationStatus());

    }

    @Test
    void createIllegalArgumentExceptionDueToId() {
        Reservation reservation = Reservation.builder()
                .id(1L)
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
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
    void updateReservationThrowNoSuchElementException() {
        Reservation reservation = Reservation.builder()
                .userId(100L)
                .roomId(777L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        Assertions.assertThrows(NoSuchElementException.class,
                () -> service.updatereservation(1L, reservation)
                );
    }

    @Test
    void updateReservationThrowIllegalStateException() {
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
        entity.setReservationStatus(ReservationStatus.APPROVED);

        when(repository.findById(any(Long.class))).thenReturn(Optional.of(entity));

        Assertions.assertThrows(IllegalStateException.class,
                () -> service.updatereservation(1L, reservation)
        );

    }

    @Test
    void updateReservation() {
        Reservation beforeUpdating = Reservation.builder()
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

        ReservationEntity entity = ReservationMapper.toEntity(beforeUpdating);
        entity.setId(1L);
        entity.setReservationStatus(ReservationStatus.PENDING);


        when(repository.findById(any(Long.class))).thenReturn(Optional.of(entity));

        Reservation updatereservation = service.updatereservation(1L, afterUpdating);

        Assertions.assertEquals(LocalDate.parse("15.01.2025", formatter), updatereservation.getEndDate());
    }

    @Test
    void approveReservationThrowNoSuchElementException() {
        Assertions.assertThrows(NoSuchElementException.class,
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

        ReservationEntity reservationEntity1 = ReservationMapper.toEntity(reservation1);
        reservationEntity1.setId(1L);
        reservationEntity1.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation2 = Reservation.builder()
                .userId(200L)
                .roomId(2L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity2 = ReservationMapper.toEntity(reservation2);
        reservationEntity2.setId(2L);
        reservationEntity2.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation3 = Reservation.builder()
                .userId(300L)
                .roomId(1L)
                .startDate(LocalDate.parse("03.01.2025", formatter))
                .endDate(LocalDate.parse("10.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity3 = ReservationMapper.toEntity(reservation3);
        reservationEntity3.setId(3L);
        reservationEntity3.setReservationStatus(ReservationStatus.PENDING);

        List<ReservationEntity> allEntity = List.of(reservationEntity1, reservationEntity2, reservationEntity3);

        when(repository.findById(3L)).thenReturn(Optional.of(reservationEntity3));
        when(repository.findAll()).thenReturn(allEntity);

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

        ReservationEntity reservationEntity1 = ReservationMapper.toEntity(reservation1);
        reservationEntity1.setId(1L);
        reservationEntity1.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation2 = Reservation.builder()
                .userId(200L)
                .roomId(2L)
                .startDate(LocalDate.parse("01.01.2025", formatter))
                .endDate(LocalDate.parse("05.01.2025", formatter))
                .build();

        ReservationEntity reservationEntity2 = ReservationMapper.toEntity(reservation2);
        reservationEntity2.setId(2L);
        reservationEntity2.setReservationStatus(ReservationStatus.APPROVED);

        Reservation reservation3 = Reservation.builder()
                .userId(300L)
                .roomId(1L)
                .startDate(LocalDate.parse("03.02.2025", formatter))
                .endDate(LocalDate.parse("10.03.2025", formatter))
                .build();

        ReservationEntity reservationEntity3 = ReservationMapper.toEntity(reservation3);
        reservationEntity3.setId(3L);
        reservationEntity3.setReservationStatus(ReservationStatus.PENDING);

        List<ReservationEntity> allEntity = List.of(reservationEntity1, reservationEntity2, reservationEntity3);

        when(repository.findById(3L)).thenReturn(Optional.of(reservationEntity3));
        when(repository.findAll()).thenReturn(allEntity);
        when(repository.save(any(ReservationEntity.class))).thenAnswer(inv -> {
            ReservationEntity arg = inv.getArgument(0);
            return arg;
        });

        Reservation reservation = service.approveReservation(3L);
        Assertions.assertEquals(ReservationStatus.APPROVED, reservation.getReservationStatus());

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