package com.example.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ConcertDto {

    @Getter
    @NoArgsConstructor
    public static class ConcertRequest {


        @NotBlank(message = "공연 제목은 필수입니다.")
        private String title;

        @NotBlank(message = "공연 장소는 필수입니다.")
        private String venue;

        @NotNull(message = "공연 날짜는 필수입니다.")
        private LocalDateTime concertDate;

        @NotNull(message = "공연 시작 시간은 필수입니다.")
        private LocalDateTime openAt;

        @NotNull(message = "공연 종료 시간은 필수입니다.")
        private LocalDateTime closeAt;

        @Valid
        @NotEmpty(message = "좌석 그룹은 최소 1개 이상이어야 합니다.")
        private List<SeatGroup> seatGroups;

        @Getter
        @NoArgsConstructor
        public static class SeatGroup {
            @NotBlank(message = "좌석 등급은 필수입니다.")
            private String grade; // VIP, R, S 등

            @Positive(message = "좌석 개수는 1개 이상이어야 합니다.")
            private int count;

            @Positive(message = "좌석 가격은 0보다 커야 합니다.")
            private int price;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class ConcertResponse {
        private final Long id;
        private final String title;
        private final String venue;
        private final LocalDateTime concertDate;
    }

    @Getter
    @AllArgsConstructor
    public static class ConcertDetailResponse {
        private Long id;
        private String title;
        private String venue;
        private LocalDateTime concertDate;
        private List<SeatDto> seats;

        @Getter
        @AllArgsConstructor
        public static class SeatDto {
            private String seatNumber;
            private int price;
            private String status;
        }
    }


}