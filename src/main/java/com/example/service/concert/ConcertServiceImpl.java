package com.example.service.concert;

import com.example.domain.concert.Concert;
import com.example.domain.concert.Seat;
import com.example.dto.ConcertDto;
import com.example.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;

    @Override
    @Transactional
    public void createConcertWithSeats(ConcertDto.ConcertRequest request){
        Concert concert = Concert.builder()
                .title(request.getTitle())
                .venue(request.getVenue())
                .concertDate(request.getConcertDate())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .build();

        List<Seat> seatList = new ArrayList<>();
        for (ConcertDto.ConcertRequest.SeatGroup group : request.getSeatGroups()) {
            for (int i = 1; i <= group.getCount(); i++) {
                String seatNumber = group.getGrade() + "-" + i;
                Seat seat = Seat.builder()
                        .concert(concert)
                        .seatNumber(seatNumber)
                        .price(group.getPrice())
                        .build();
                seatList.add(seat);
            }
        }

        concert.getSeats().addAll(seatList);
        concertRepository.save(concert);
    }

    @Override
    @Cacheable(value =  "concertList")
    @Transactional(readOnly = true)
    public List<ConcertDto.ConcertResponse> getConcertList() {
        return concertRepository.findAll().stream()
                .map(c -> new ConcertDto.ConcertResponse(
                        c.getId(), c.getTitle(), c.getVenue(), c.getConcertDate()))
                .toList();
    }

    @Override
    @Cacheable(value = "concerts", key = "#concertId")
    @Transactional(readOnly = true)
    public ConcertDto.ConcertDetailResponse getConcertDetail(Long concertId) {
        log.info("DB에서 공연 상세 조회: concertId={}", concertId);
        Concert concert = concertRepository.findWithSeatsById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("해당 콘서트를 찾을 수 없습니다."));

        List<ConcertDto.ConcertDetailResponse.SeatDto> seats = concert.getSeats().stream()
                .map(seat -> new ConcertDto.ConcertDetailResponse.SeatDto(
                        seat.getSeatNumber(),
                        seat.getPrice(),
                        seat.getStatus().name()))
                .toList();

        return new ConcertDto.ConcertDetailResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getVenue(),
                concert.getConcertDate(),
                seats
        );
    }
}

