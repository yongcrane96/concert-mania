package com.example.service.reservation;

public interface ReservationService {
    void reserveSeat(Long seatId, Long userId);
    void payReservation(Long reservationId);
}
