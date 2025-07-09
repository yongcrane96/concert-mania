package com.example.controller.admin;

import com.example.dto.ConcertDto;
import com.example.service.concert.ConcertServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/concerts")
@RequiredArgsConstructor
public class ConcertAdminController {

    private final ConcertServiceImpl concertServiceImpl;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createConcert(@RequestBody ConcertDto.ConcertRequest request) {
        concertServiceImpl.createConcertWithSeats(request);
        return ResponseEntity.ok("콘서트 등록 및 좌석 생성 완료");
    }
}
