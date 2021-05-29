package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.AirlineSeat;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.airline.AirlineSearchDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.repository.AirlinSeatRepository;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@Transactional
@Rollback(value = false)
@Slf4j
public class AirlineServiceTest {

    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    AirlineService airlineService;
    @Autowired
    AirlinSeatRepository airlinSeatRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    EntityManager em;

    @Test
    public void testSearch(){
        AirlineSearchDto airlineSearchDto = new AirlineSearchDto(Departure.JEJU, Distination.BUS,
                LocalDate.of(2021,05,20)
                ,1,2);

        AirlineSearchDto airlineSearchDto1 = null;
        List<Airline> airlineList = airlineService.searchAirlines(airlineSearchDto);

        for (Airline a: airlineList) {
            System.out.println("항공권="+a.getId());
        }
        //AirlineService.searchAirline 메서드 테스트
        //Assertions.assertThat(airlineList.get(0).getId()).isEqualTo(26);
        //Assertions.assertThat(airlineList.get(1).getId()).isEqualTo(27);

    }

   /* @Test
    public void testRUD(){
        Airline findAirline = airlineService.findById(2L);
        assertThat(findAirline.getId()).isEqualTo(2L);
        //단건 조회 메서드 테스트
        Page<Airline> findAllAirline = airlineService.findAllAirline();
        System.out.println(findAllAirline);
        //항공권 all조회 메서드 테스트

        findAirline.updateDeparture(Departure.BUS);
        airlineService.updateAirline(findAirline);
        em.flush();
        em.clear();
        Airline findAirline2 = airlineService.findById(2L);
        assertThat(findAirline.getDeparture()).isEqualTo(Departure.BUS);
        //수정 메서드 테스트

        //airlineService.deleteAirline(findAirline.getId());
        //삭제는 h2에서 테이블 생성시 폴인키가 묶여 삭제가 안됨. mariadb로 설정시 테스트 할것

    }*/
    @Test
    public void testCreate(){
        Departure departure = Departure.JEJU;
        Distination distination = Distination.DAE;
        LocalDate depart_date = LocalDate.of(2021,04,03);
        LocalTime depart_time = LocalTime.of(12,45);
        LocalTime reach_time = LocalTime.of(13,55);

        Airline airline = Airline.createAirline(departure,distination,depart_date,
                depart_time,reach_time,9);

        airlineService.createAirline(airline);
    }

    @BeforeEach
    public void before(){
        Departure departure1 = Departure.ICN;
        Distination distination1 = Distination.JEJU;
        LocalDate depart_date1 = LocalDate.of(2021,04,03);
        LocalTime depart_time1 = LocalTime.of(12,45);
        LocalTime reach_time1 = LocalTime.of(13,55);

        Departure departure2 = Departure.ICN;
        Distination distination2 = Distination.JEJU;
        LocalDate depart_date2 = LocalDate.of(2021,04,01);
        LocalTime depart_time2 = LocalTime.of(16,25);
        LocalTime reach_time2 = LocalTime.of(17,35);


        Departure departure3 = Departure.ICN;
        Distination distination3 = Distination.JEJU;
        LocalDate depart_date3 = LocalDate.of(2021,04,10);
        LocalTime depart_time3 = LocalTime.of(12,20);
        LocalTime reach_time3 = LocalTime.of(13,30);

        Departure departure4 = Departure.ICN;
        Distination distination4 = Distination.DAE;
        LocalDate depart_date4 = LocalDate.of(2021,04,15);
        LocalTime depart_time4 = LocalTime.of(12,45);
        LocalTime reach_time4 = LocalTime.of(13,55);

        for (int i = 1; i < 10; i++) {
            Seat seat = Seat.createSeat("A"+i);
            seatRepository.save(seat);
        }

        List<Seat> seatList = seatRepository.findAll();
        //원래는 db에 좌석들의 값이 있어야 하지만 초기 테스트이므로 직접 생성해 넣어줌

        Airline airline1 = Airline.createAirline(departure1,distination1, depart_date1,
                depart_time1,reach_time1,9);
        airline1.setSeat(seatList);
        airlineRepository.save(airline1);
        Airline airline2 = Airline.createAirline(departure2,distination2, depart_date2,
                depart_time2,reach_time2,9);
        airline1.setSeat(seatList);
        airlineRepository.save(airline2);
        Airline airline3 = Airline.createAirline(departure3,distination3, depart_date3,
                depart_time3,reach_time3,9);
        airline1.setSeat(seatList);
        airlineRepository.save(airline3);
        Airline airline4 = Airline.createAirline(departure4,distination4, depart_date4,
                depart_time4,reach_time4,9);
        airline1.setSeat(seatList);
        airlineRepository.save(airline4);


        /*for (Seat s: seatList) {
            AirlineSeat airlineSeats = AirlineSeat.createAirlineSeat(airline1,s);
            airlinSeatRepository.save(airlineSeats);
        }
        for (Seat s: seatList) {
            AirlineSeat airlineSeats = AirlineSeat.createAirlineSeat(airline2,s);
            airlinSeatRepository.save(airlineSeats);
        }
        for (Seat s: seatList) {
            AirlineSeat airlineSeats = AirlineSeat.createAirlineSeat(airline3,s);
            airlinSeatRepository.save(airlineSeats);
        }
        for (Seat s: seatList) {
            AirlineSeat airlineSeats = AirlineSeat.createAirlineSeat(airline4,s);
            airlinSeatRepository.save(airlineSeats);
        }*/
        List<AirlineSeat> airlineSeat = airlinSeatRepository.findAll();

        //airline1.setSeat(airlineSeat);

        //서치값 초기화 하면서 끝남
    }
}