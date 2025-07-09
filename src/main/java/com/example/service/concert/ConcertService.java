package com.example.service.concert;


import com.example.dto.ConcertDto;

import java.util.List;

public interface ConcertService {
    void createConcertWithSeats(ConcertDto.ConcertRequest request);
    List<ConcertDto.ConcertResponse> getConcertList();
    ConcertDto.ConcertDetailResponse getConcertDetail(Long concertId);
}