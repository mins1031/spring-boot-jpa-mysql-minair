package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.AirlineSeat;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
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
        AirlineGenerateDto airlineGenerateDto1 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,04,03))
                .departTime(LocalTime.of(12,45))
                .reachTime(LocalTime.of(13,55))
                .build();
        Airline airline = Airline.createAirline(airlineGenerateDto1);

        airlineService.createAirline(airline);
    }

}