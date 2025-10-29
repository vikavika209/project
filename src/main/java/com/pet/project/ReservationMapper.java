package com.pet.project;

import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public static Reservation toReservation(ReservationEntity entity){
        return new Reservation(entity.getId(),
                entity.getUserId(),
                entity.getRoomId(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getReservationStatus()
        );
    }

    public static ReservationEntity toEntity (Reservation reservation){
        return new ReservationEntity(reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                reservation.getReservationStatus()
        );
    }
}
