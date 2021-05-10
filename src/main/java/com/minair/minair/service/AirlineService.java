package com.minair.minair.service;

import ch.qos.logback.classic.sift.AppenderFactoryUsingJoran;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.AirlineSearchDto;
import com.minair.minair.repository.AirlinSeatRepository;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final SeatRepository seatRepository;
    private final SeatService seatService;

    @Transactional
    public void createAirline(Airline airline){
        List<Seat> seatList = seatRepository.findAll();

        airlineRepository.save(airline);

        seatService.createSeats(airline,airline.getSeatcount());
    }

    @Transactional
    public void updateAirline(Airline airline){
        Optional<Airline> optionalAirline = airlineRepository.findById(airline.getId());
        Airline findAirline = optionalAirline.get();
        findAirline.updateAirline(airline);
    }

    public Airline findById(Long id){
        Optional<Airline> optionalAirline = airlineRepository.findById(id);
        return optionalAirline.get();
    }

    public Page<Airline> findAllAirline(Pageable pageable){

        return airlineRepository.allAirlineList(pageable);
    }

    @Transactional
    public void deleteAirline(Long id){
        airlineRepository.deleteById(id);
    }
    //삭제는 폴인키가 묶여있어 삭제가 어려움 일단 보류.

    @Transactional
    public List<Airline> searchAirlines(AirlineSearchDto airlineSearchDto){
        System.out.println(airlineSearchDto.getDeparture().getClass());
        System.out.println(airlineSearchDto.getDistination());
        System.out.println(airlineSearchDto.getDepart_date());
        //System.out.println(airlineSearchDto.getComback_date());
        System.out.println(airlineSearchDto.getAdult());
        System.out.println(airlineSearchDto.getChild());

        log.info("항공권 검색");
        List<Airline> findSearchList = airlineRepository.searchResults(airlineSearchDto);
        return findSearchList;
    }

    @Transactional
    public void subSeatCount(Long airlineId, int totalPerson){
        System.out.println(airlineId);
        System.out.println(totalPerson);
        Optional<Airline> optionalAirline = airlineRepository.findById(airlineId);
        Airline airline = optionalAirline.get();
        airline.discountSeat(totalPerson);
    }

}
