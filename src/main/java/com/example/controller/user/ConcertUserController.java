package com.example.controller.user;

import com.example.dto.ConcertDto;
import com.example.service.concert.ConcertServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Concert", description = "콘서트 조회 API")
@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertUserController {

    private final ConcertServiceImpl concertServiceImpl;

    @Operation(summary = "콘서트 목록 조회", description = "전체 콘서트 리스트를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ConcertDto.ConcertResponse>> getConcertList() {
        return ResponseEntity.ok(concertServiceImpl.getConcertList());
    }

    @Operation(summary = "콘서트 상세 조회", description = "특정 콘서트 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ConcertDto.ConcertDetailResponse> getConcertDetail(@PathVariable Long id) {
        return ResponseEntity.ok(concertServiceImpl.getConcertDetail(id));
    }
}
