package com.example.controller.user;

import com.example.domain.payment.Payment;
import com.example.service.payment.PaymentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentServiceImpl paymentServiceImpl;

    @Operation(summary = "예약 결제 처리", description = "예약 ID로 결제를 수행합니다.")
    @PostMapping("/{reservationId}")
    public ResponseEntity<String> pay(@Parameter(description = "결제할 예약 ID", required = true)
                                          @PathVariable Long reservationId) {
        Payment payment = paymentServiceImpl.createPayment(reservationId);

        if (payment.getStatus().isSuccess()) {
            return ResponseEntity.ok("결제가 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("결제에 실패했습니다. 다시 시도해주세요.");
        }
    }
}
