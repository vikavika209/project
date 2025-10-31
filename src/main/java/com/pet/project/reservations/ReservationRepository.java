package com.pet.project.reservations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

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

    @Query("""
        SELECT r.id FROM ReservationEntity r
            WHERE r.roomId = :roomId
                AND :startDate < r.endDate
                AND r.startDate < :endDate
                AND r.reservationStatus = :status
    """)
    List<Long> findConflictReservation(
            @Param("roomId") Long roomId,
            @Param("startDate")LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") ReservationStatus status
    );

    @Query("""
       SELECT r FROM ReservationEntity r
           WHERE (:roomId IS NULL OR r.roomId = :roomId)
           AND (:userId IS NULL OR r.userId = :userId)
           AND (:status IS NULL OR r.reservationStatus = :status)
    """)
    List<ReservationEntity> searchAllByFilter(
            @Param("roomId") Long roomId,
            @Param("userId") Long userId,
            @Param("status") ReservationStatus status,
            Pageable pageable
    );
}
