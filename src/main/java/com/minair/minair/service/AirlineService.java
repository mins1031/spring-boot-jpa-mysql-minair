package com.minair.minair.service;

import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.airline.AirlineCreateDto;
import com.minair.minair.domain.dto.airline.AirlineDto;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.exception.PageNumberException;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.AirlineSearchDto;
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
    private final ModelMapper modelMapper;

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

        Airline airline = Airline.createAirline(airlineGenerateDto);

        Airline savedAirline = airlineRepository.save(airline);
        //System.out.println(savedAirline.getSeats());
        seatService.createSeats(savedAirline, airline.getSeatCount());

        /*List<Seat> byAirline_id = seatRepository.findByAirline_Id(savedAirline.getId());
        savedAirline.setSeat(byAirline_id);
*/

        AirlineDto airlineDto = modelMapper.map(airline, AirlineDto.class);

        return airlineDto;
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
