package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.repository.SeatRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional(readOnly = true)
@Rollback(value = false)
class SeatServiceTest {

    @Autowired
    SeatService seatService;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    AirlineService airlineService;

    @Test
    public void CTest(){

        AirlineGenerateDto airlineGenerateDto = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,04,03))
                .departTime(LocalTime.of(12,45))
                .reachTime(LocalTime.of(13,55))
                .build();

        Airline airline = Airline.createAirline(airlineGenerateDto);

        //airlineService.createAirline(airline);

        seatService.createSeats(airline,airline.getSeatCount());
    }
}