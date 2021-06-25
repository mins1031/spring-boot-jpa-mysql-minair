package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.AirlineSeat;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.airline.*;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.exception.PageNumberException;
import com.minair.minair.repository.AirlinSeatRepository;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @BeforeEach
    public void before() {
        AirlineGenerateDto airlineGenerateDto4 = AirlineGenerateDto.builder()
                .departure(Departure.BUS)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline4 = Airline.createAirline(airlineGenerateDto4);
        AirlineGenerateDto airlineGenerateDto5 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.ICN)
                .departDate(LocalDate.of(2021,06,20))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline5 = Airline.createAirline(airlineGenerateDto5);
        AirlineGenerateDto airlineGenerateDto6 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.ICN)
                .departDate(LocalDate.of(2021,06,20))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline6 = Airline.createAirline(airlineGenerateDto6);
        AirlineGenerateDto airlineGenerateDto7 = AirlineGenerateDto.builder()
                .departure(Departure.ICN)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,06,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline7 = Airline.createAirline(airlineGenerateDto7);
        AirlineGenerateDto airlineGenerateDto8 = AirlineGenerateDto.builder()
                .departure(Departure.ICN)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,06,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline8 = Airline.createAirline(airlineGenerateDto8);
        AirlineGenerateDto airlineGenerateDto9 = AirlineGenerateDto.builder()
                .departure(Departure.GWANG)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline9 = Airline.createAirline(airlineGenerateDto9);
        AirlineGenerateDto airlineGenerateDto10 = AirlineGenerateDto.builder()
                .departure(Departure.GWANG)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();

        airlineRepository.save(airline4);
        airlineRepository.save(airline5);
        airlineRepository.save(airline6);
        airlineRepository.save(airline7);
        airlineRepository.save(airline8);
        airlineRepository.save(airline9);

    }

    @Test
    @DisplayName("항공권 생성 테스트")
    void createAirlineTest() {
        AirlineCreateDto airlineCreateDto = AirlineCreateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.BUS)
                .depart_date(LocalDate.of(2021,06,02))
                .depart_time(LocalTime.of(13,25))
                .reach_time(LocalTime.of(14,00))
                .build();

        AirlineDto createdAirline = airlineService.createAirline(airlineCreateDto);

        System.out.println(createdAirline);
        assertEquals(createdAirline.getDeparture(), airlineCreateDto.getDeparture());
        assertEquals(createdAirline.getDistination(), airlineCreateDto.getDistination());
        assertEquals(createdAirline.getDepart_date(), airlineCreateDto.getDepart_date());
        assertEquals(createdAirline.getDepart_time(), airlineCreateDto.getDepart_time());
    }

    @Test
    @DisplayName("모든 항공권 조회 테스트")
    void generalFindAllAirlineTest() {
        int pageNum = 1;
        int totalAirlineCount = 6;
        QueryAirlinesDto allAirline = airlineService.findAllAirline(pageNum);
        List<AirlineDto> allAirlines = (List<AirlineDto>) allAirline.getAirlineList();

        assertEquals(totalAirlineCount,allAirlines.size());
        assertEquals(totalAirlineCount,allAirline.getPageDto().getTotal());
    }

    @Test
    @DisplayName("pageNum 값이 0인경우 모든 항공권 조회 테스트")
    @Rollback
    @Disabled
    void badRequestCaseFindAllAirlineTest() {
        int pageNum = 0;

        assertThrows(PageNumberException.class, () -> airlineService.findAllAirline(pageNum));
    }

    @Test
    @DisplayName("항공권이 없는경우 모든 항공권 조회 테스트(@beforeEach 지우고 실행)")
    @Rollback
    void notFindAllAirlineTest() {
        int pageNum = 1;

        assertThrows(NullPointerException.class, () -> airlineService.findAllAirline(pageNum));
    }

    @Test
    @DisplayName("항공권 검색 테스트")
    void searchAirlinesTest() {
        Departure departure = Departure.JEJU;
        Distination distination = Distination.ICN;
        LocalDate dateOfDepart = LocalDate.of(2021,06,20);
        LocalDate dateOfComeback = LocalDate.of(2021,06,23);
        int adult = 1;
        int child = 1;

        AirlineSearchDto airlineSearchDto = AirlineSearchDto.builder()
                .departure(departure)
                .distination(distination)
                .depart_date(dateOfDepart)
                .comebackDate(dateOfComeback)
                .adult(adult)
                .child(child)
                .build();

        AirlineSearchApiDto airlineSearchApiDto = airlineService.searchAirlines(airlineSearchDto);
        List<AirlineDto> goAirlines = (List<AirlineDto>) airlineSearchApiDto.getGoAirlineList();
        List<AirlineDto> backAirlines = (List<AirlineDto>) airlineSearchApiDto.getBackAirlineList();

        assertEquals(airlineSearchDto.getDeparture(),goAirlines.get(0).getDeparture());
        assertEquals(airlineSearchDto.getDeparture(),goAirlines.get(1).getDeparture());
        assertEquals(airlineSearchDto.getDistination(),goAirlines.get(0).getDistination());
        assertEquals(airlineSearchDto.getDistination(),goAirlines.get(1).getDistination());
        assertEquals(airlineSearchDto.getDepart_date(),goAirlines.get(0).getDepart_date());
        assertEquals(airlineSearchDto.getComebackDate(),backAirlines.get(0).getDepart_date());
        assertEquals(airlineSearchDto.getAdult(),airlineSearchApiDto.getAdult());
        assertEquals(airlineSearchDto.getChild(),airlineSearchApiDto.getChild());
        assertEquals(airlineSearchDto.getAdult()+airlineSearchDto.getChild(),airlineSearchApiDto.getTotalPerson());
    }

    @Test
    @DisplayName("없는 항공권 검색 테스트")
    @Rollback
    void notSearchAirlinesTest() {
        Departure departure = Departure.DAE;
        Distination distination = Distination.ICN;
        LocalDate dateOfDepart = LocalDate.of(2021,06,20);
        LocalDate dateOfComeback = LocalDate.of(2021,06,23);
        int adult = 1;
        int child = 1;

        AirlineSearchDto airlineSearchDto = AirlineSearchDto.builder()
                .departure(departure)
                .distination(distination)
                .depart_date(dateOfDepart)
                .comebackDate(dateOfComeback)
                .adult(adult)
                .child(child)
                .build();

        assertThrows(NotFoundAirlines.class, () -> airlineService.searchAirlines(airlineSearchDto));
    }

    @Test
    void subSeatCount() {
    }

    @Test
    void plusSeatCount() {
    }



//============보류 & 추후 구현 예정 기능 테스트=========================
    @Test
    @Disabled
    void deleteAirline() {
    }

    @Test
    @Disabled
    void updateAirline() {
    }

    @Test
    @Disabled
    void findById() {
    }
}