package com.example.service;

import com.example.domain.concert.Concert;
import com.example.domain.concert.Seat;
import com.example.domain.reservation.Reservation;
import com.example.domain.user.User;
import com.example.domain.user.UserRole;

import java.time.LocalDateTime;

public class TestFixtureFactory {

    public static User createTestUser() {
        return User.builder()
                .email("test@example.com")
                .password("hashed_pw")
                .role(UserRole.USER)
                .build();
    }

    public static Concert createTestConcert() {
        LocalDateTime now = LocalDateTime.now();
        return Concert.builder()
                .title("블랙핑크 콘서트")
                .venue("올림픽 A홀")
                .concertDate(now.plusHours(1))
                .openAt(now.minusMinutes(10))
                .closeAt(now.plusHours(2))
                .build();
    }

    public static Seat createTestSeat(Concert concert) {
        return Seat.builder()
                .concert(concert)
                .seatNumber("A1")
                .price(10000)
                .build();
    }

    public static Reservation createTestReservation(User user, Seat seat) {
        return Reservation.builder()
                .user(user)
                .seat(seat)
                .build();
    }

    public static Reservation createPendingReservation() {
        User user = createTestUser();
        Concert concert = createTestConcert();
        Seat seat = createTestSeat(concert);
        seat.reserve();

        Reservation reservation = Reservation.builder()
                .user(user)
                .seat(seat)
                .build();
        return reservation;
    }
}

