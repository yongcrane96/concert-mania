package com.example.repository;

import com.example.domain.concert.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    @Query("SELECT s FROM Seat s WHERE s.status = 'OCCUPIED' AND s.occupiedAt <= :limitTime")
    List<Seat> findExpiredOccupiedSeats(LocalDateTime limitTime);

}