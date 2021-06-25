package com.minair.minair.service;

import com.minair.minair.common.ServerConstValue;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.airline.*;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.exception.PageNumberException;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.domain.Airline;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.engine.Mode;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final SeatService seatService;
    private final ServerConstValue serverConstValue;

    @Transactional
    public AirlineDto createAirline(AirlineCreateDto airlineCreateDto){
        if (airlineCreateDto == null)
            throw new RequestNullException();

        AirlineGenerateDto airlineGenerateDto =
                AirlineGenerateDto.builder()
                        .departure(airlineCreateDto.getDeparture())
                        .distination(airlineCreateDto.getDistination())
                        .departDate(airlineCreateDto.getDepart_date())
                        .departTime(airlineCreateDto.getDepart_time())
                        .reachTime(airlineCreateDto.getReach_time())
                        .build();

        Airline savedAirline = airlineRepository.save(Airline.createAirline(airlineGenerateDto));
        seatService.createSeats(savedAirline, savedAirline.getSeatCount());

        AirlineDto airlineDto1 = AirlineDto.airlineDto(savedAirline);

        return airlineDto1;
    }

    @Transactional
    public void updateAirline(Airline airline){
        Optional<Airline> optionalAirline = airlineRepository.findById(airline.getId());
        Airline findAirline = optionalAirline.get();
        findAirline.updateAirline(airline);
    }

    public AirlineDto findById(Long id){
        Optional<Airline> optionalAirline = airlineRepository.findById(id);
        AirlineDto findAirline = AirlineDto.airlineDto(optionalAirline.get());

        return findAirline;
    }

    public QueryAirlinesDto findAllAirline(int pageNum){

        if (pageNum == 0)
            throw new PageNumberException();

        int offset = pageNum -1;

        PageRequest pageRequest = PageRequest.of(offset,serverConstValue.getLimit());
        Page<Airline> allAirline = airlineRepository.allAirlineList(pageRequest);
        if (allAirline.getContent().isEmpty())
            throw new NullPointerException();

        List<AirlineDto> resultAirlines = allAirline.getContent().stream()
                .map(airline -> AirlineDto.airlineDto(airline))
                .collect(Collectors.toList());

        PageDto pageDto = new PageDto(pageNum,10,allAirline.getTotalElements(),allAirline.getTotalPages());

        QueryAirlinesDto queryAirlinesDto = QueryAirlinesDto.builder()
                .airlineList(resultAirlines)
                .pageDto(pageDto)
                .build();

        return queryAirlinesDto;
    }

    @Transactional
    public void deleteAirline(Long id){
        airlineRepository.deleteById(id);
    }
    //삭제는 폴인키가 묶여있어 삭제가 어려움 일단 보류.

    @Transactional
    public AirlineSearchApiDto searchAirlines(AirlineSearchDto airlineSearchDto){
        if (airlineSearchDto == null)
            throw new RequestNullException();

        log.info("항공권 검색");
        List<Airline> findSearchList = airlineRepository.searchResults(airlineSearchDto);

        if (findSearchList.isEmpty())
            throw new NotFoundAirlines();

        List<AirlineDto> goAirlineList = findSearchList.stream()
                .map(airline -> AirlineDto.airlineDto(airline))
                .collect(Collectors.toList());
        //1) 가는편 항공권 리스트 DTO변환

        String convertDeparture = airlineSearchDto.getDeparture().toString();
        String convertDistination = airlineSearchDto.getDistination().toString();

        Departure departure = Departure.valueOf(convertDistination);
        Distination distination = Distination.valueOf(convertDeparture);

        AirlineSearchDto backAirlineDto = new AirlineSearchDto(departure,distination,
                airlineSearchDto.getComebackDate(),airlineSearchDto.getAdult(),airlineSearchDto.getChild());
        //2) 오는편 조회를 위해 출,도착지 정보 변경해 AirlineSearchDto 생성

        List<Airline> backList = airlineRepository.searchResults(backAirlineDto);
        List<AirlineDto> backAirlineList = backList.stream()
                .map(airline -> AirlineDto.airlineDto(airline))
                .collect(Collectors.toList());
        //3) 오는편 항공권 조회후 DTO변환

        AirlineSearchApiDto<List<AirlineDto>> airlineSearchApiResult
                = new AirlineSearchApiDto<>(goAirlineList,backAirlineList,airlineSearchDto.getAdult(),
                airlineSearchDto.getChild());

        return airlineSearchApiResult;
        //4) 총괄 DTO로 변환해 리턴
    }

    @Transactional
    public void subSeatCount(Long airlineId, int totalPerson){
        if (airlineId == null && totalPerson == 0)
            throw new RequestNullException();
        Optional<Airline> optionalAirline = airlineRepository.findById(airlineId);
        Airline airline = optionalAirline.get();
        if (airline == null)
            throw new NotFoundAirlines();

        airline.discountSeat(totalPerson);
    }

    @Transactional
    public void plusSeatCount(Long airlineId, int totalPerson){
        if (airlineId == null && totalPerson == 0)
            throw new RequestNullException();

        Optional<Airline> optionalAirline = airlineRepository.findById(airlineId);
        Airline airline = optionalAirline.get();
        if (airline == null)
            throw new NotFoundAirlines();

        airline.plusSeat(totalPerson);
    }

}
