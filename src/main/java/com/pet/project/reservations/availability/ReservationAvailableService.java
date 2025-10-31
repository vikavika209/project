package com.pet.project.reservations.availability;

import com.pet.project.reservations.ReservationRepository;
import com.pet.project.reservations.ReservationStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationAvailableService {

    ReservationRepository repo;

    public boolean isAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ){
        List<Long> allWithConflict = repo.findConflictReservation(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );

        if (allWithConflict.isEmpty()){
            return true;
        }

        String collect = allWithConflict.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        log.error("Conflict with reservations: {}", collect);

        return false;
    }
}
