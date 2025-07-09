package com.example.integration;

import com.example.domain.concert.Seat;
import com.example.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SeatReservationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("좌석 임시 점유 성공 테스트")
    void reserveSeatSuccess() throws Exception {
        Long seatId = 1L;
        Long userId = 1L;

        ResultActions result = mockMvc.perform(post("/api/reservations/seat/{seatId}/user/{userId}/reserve", seatId, userId)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk());
    }
}
