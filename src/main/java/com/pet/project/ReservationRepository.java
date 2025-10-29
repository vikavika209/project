package com.pet.project;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    @Modifying
    @Query(
            "update ReservationEntity e " +
                    "set e.reservationStatus = :status " +
                    "where e.id = :id"
    )
    void setStatus(
                   @Param("id") Long id,
                   @Param("status") ReservationStatus reservationStatus
    );
}
