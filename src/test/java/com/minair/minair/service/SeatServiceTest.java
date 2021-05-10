package com.minair.minair.service;

import com.minair.minair.domain.Airline;
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
        Departure departure = Departure.JEJU;
        Distination distination = Distination.DAE;
        LocalDate depart_date = LocalDate.of(2021,04,03);
        LocalTime depart_time = LocalTime.of(12,45);
        LocalTime reach_time = LocalTime.of(13,55);

        Airline airline = Airline.createAirline(departure,distination,depart_date,
                depart_time,reach_time,18);

        //airlineService.createAirline(airline);

        seatService.createSeats(airline,airline.getSeatcount());
    }
}