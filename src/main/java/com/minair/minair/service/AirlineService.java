package com.minair.minair.service;

import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.exception.PageNumberException;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.AirlineSearchDto;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        if (airline == null)
            throw new RequestNullException();
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

    public Page<Airline> findAllAirline(int pageNum){

        if (pageNum == 0)
            throw new PageNumberException();

        int offset = pageNum -1;

        PageRequest pageRequest = PageRequest.of(offset,10);
        Page<Airline> airlinePage = airlineRepository.allAirlineList(pageRequest);
        if (airlinePage.getContent().isEmpty())
            throw new NullPointerException();
        return airlinePage ;
    }

    @Transactional
    public void deleteAirline(Long id){
        airlineRepository.deleteById(id);
    }
    //삭제는 폴인키가 묶여있어 삭제가 어려움 일단 보류.

    @Transactional
    public List<Airline> searchAirlines(AirlineSearchDto airlineSearchDto){
        if (airlineSearchDto == null)
            throw new RequestNullException();

        log.info("항공권 검색");
        List<Airline> findSearchList = airlineRepository.searchResults(airlineSearchDto);
        if (findSearchList == null)
            throw new NotFoundAirlines();
        return findSearchList;
    }

    @Transactional
    public void subSeatCount(Long airlineId, int totalPerson){
        if (airlineId == null && totalPerson == 0)
            throw new RequestNullException();
        Optional<Airline> optionalAirline = airlineRepository.findById(airlineId);
        Airline airline = optionalAirline.get();
        if (airline == null)
            throw new NullPointerException();

        airline.discountSeat(totalPerson);
    }

    @Transactional
    public void plusSeatCount(Long airlineId, int totalPerson){
        if (airlineId == null && totalPerson == 0)
            throw new RequestNullException();
        Optional<Airline> optionalAirline = airlineRepository.findById(airlineId);
        Airline airline = optionalAirline.get();
        if (airline == null)
            throw new NullPointerException();

        airline.plusSeat(totalPerson);
    }

}
