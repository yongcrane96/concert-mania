package com.example.dto;
import lombok.Getter;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReservationDto {

    @Getter
    @NoArgsConstructor
    public static class PaymentRequest {
        @NotNull(message = "예약 ID는 필수입니다.")
        private Long reservationId;
    }

    @Getter
    public static class PaymentResponse {
        private final String status;
        private final String message;

        public PaymentResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}