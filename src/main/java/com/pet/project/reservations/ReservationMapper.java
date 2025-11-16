package com.pet.project.reservations;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper
public interface ReservationMapper {

    Reservation toReservation(ReservationEntity entity);
    ReservationEntity toEntity (Reservation reservation);
}
