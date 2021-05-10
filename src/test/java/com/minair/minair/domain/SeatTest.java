package com.minair.minair.domain;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional(readOnly = true)
@Rollback(value = false)
class SeatTest {

    @Autowired
    EntityManager em;

    @Test
    public void seatTest(){
        Departure departure = Departure.JEJU;
        Distination distination = Distination.DAE;
        LocalDate depart_date = LocalDate.of(2021,04,03);
        LocalTime depart_time = LocalTime.of(12,45);
        LocalTime reach_time = LocalTime.of(13,55);

        Airline airline = Airline.createAirline(departure,distination,depart_date,
                depart_time,reach_time,18);
        Seat seat = Seat.createSeat("A4");
        seat.setAirline(airline);
        em.flush();
        em.clear();

    }
}