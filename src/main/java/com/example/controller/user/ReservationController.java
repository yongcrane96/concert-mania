package com.example.controller.user;

import com.example.dto.ReservationDto;
import com.example.service.reservation.ReservationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Reservation", description = "예매 관련 API")
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationServiceImpl reservationServiceImpl;

    @Operation(summary = "결제 완료(예약 확정) 상태로 변경", description = "임시 점유된 예약을 결제하여 확정합니다.")
    @PostMapping("/{reservationId}/pay")
    public ResponseEntity<ReservationDto.PaymentResponse> payReservation(
            @PathVariable Long reservationId) {
        reservationServiceImpl.payReservation(reservationId);
        return ResponseEntity.ok(new ReservationDto.PaymentResponse("COMPLETED", "결제 완료"));
    }

    @Operation(summary = "좌석 임시 점유 및 예약 생성", description = "좌석을 임시로 점유하여 예약 진행 상태로 만듭니다.")
    @PostMapping("/seat/{seatId}/user/{userId}/reserve")
    public ResponseEntity<String> reserveSeat(
            @PathVariable Long seatId,
            @PathVariable Long userId) {
        reservationServiceImpl.reserveSeat(seatId, userId);
        return ResponseEntity.ok("좌석이 임시 점유되었습니다. 10분 내 결제해 주세요.");
    }
}
