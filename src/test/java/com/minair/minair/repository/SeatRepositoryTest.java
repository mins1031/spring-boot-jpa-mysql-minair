package com.minair.minair.repository;

import com.minair.minair.domain.*;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.SeatStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@Transactional
class SeatRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    AirlinSeatRepository airlinSeatRepository;

    @Test
    public void testRepository(){
        //when
        String from = "ICN";
        Departure departure = Departure.ICN;
        Distination distination = Distination.JEJU;
        String depart_date = "21-04-03";
        String depart_time = "12:45";
        String reach_time = "13:55";


        //Airline airline = Airline.createAirline(departure,distination, depart_date,
         //       depart_time,reach_time,9);
        List<Seat> seats = new ArrayList<>();
        List<AirlineSeat> airlineSeats = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            Seat seat = Seat.createSeat("A"+i);
            seatRepository.save(seat);//우선 보류하고 테스트 다 해볼것
            seats.add(seat);
        }
        for (int i = 0; i < 9; i++) {
           // AirlineSeat airlineSeat = AirlineSeat.createAirlineSeat(airline,seats.get(i));
          //  airlineSeats.add(airlineSeat);
        }

        /*Optional<Seat> seat1 = seatRepository.findById(seat.getId());
        System.out.println(seat.getSeatName());
        Assertions.assertThat(seat1.get().getSeatName()).isEqualTo(seat.getSeatName());*/
    }
}