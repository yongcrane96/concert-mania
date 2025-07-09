package com.example.domain.concert;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private LocalDateTime occupiedAt;

    @Builder
    public Seat(Concert concert, String seatNumber, int price) {
        this.concert = concert;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    // 비즈니스 로직
    public void occupy(){
        if (this.status != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 점유된 좌석입니다.");
        }
        this.status = SeatStatus.OCCUPIED;
        this.occupiedAt = LocalDateTime.now();
    }

    public void reserve(){
        this.status = SeatStatus.RESERVED;
    }

    public void makeAvailable(){
        this.status = SeatStatus.AVAILABLE;
        this.occupiedAt = null;
    }
}
