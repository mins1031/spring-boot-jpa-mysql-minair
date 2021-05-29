package com.minair.minair;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.repository.AirlinSeatRepository;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import com.minair.minair.service.AirlineService;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static com.minair.minair.domain.QAirline.airline;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class SearchTest {

    @Autowired
    AirlineService airlineService;
    @Autowired
    EntityManager em;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    AirlinSeatRepository airlinSeatRepository;
    @Autowired
    SeatRepository seatRepository;

    JPAQueryFactory queryFactory;

   /* @BeforeEach
    public void before(){
        queryFactory = new JPAQueryFactory(em);
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
        Airline airline2 = Airline.createAirline(departure2,distination2, depart_date2,
                depart_time2,reach_time2,9);
        Airline airline3 = Airline.createAirline(departure3,distination3, depart_date3,
                depart_time3,reach_time3,9);
        Airline airline4 = Airline.createAirline(departure4,distination4, depart_date4,
                depart_time4,reach_time4,9);


        for (Seat s: seatList) {
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
        }
        List<AirlineSeat> airlineSeat = airlinSeatRepository.findAll();

        //airline1.setSeat(airlineSeat);
        em.flush();
        em.clear();
        //서치값 초기화 하면서 끝남
    }*/

    /*@Test
    public void airlineSearchTest(){
        AirlineSearchDto airlineSearchDto = new AirlineSearchDto(Departure.ICN, Distination.JEJU,
                LocalDate.of(2021,04,03)
                ,1,2);

        List<Airline> airlineList = queryFactory
                .select(airline)
                .from(airline)
                .where(airline.departure.eq(airlineSearchDto.getDeparture())
                        .and(airline.distination.eq(airlineSearchDto.getDistination()))
                        .and(airline.depart_date.goe(airlineSearchDto.getDepart_date()))
                        .and(airline.aboveseat.goe(airlineSearchDto.getAdult()+
                                airlineSearchDto.getChild())))
                .fetch();
//.join(airline.airlineSeats,airlineSeat).fetchJoin()//.on(airline.id.eq(airlineSeat.airline().id))
//!!!!!!!!!!!!!!!04/09 19:14 항공권 조회 성공.위 로직이 조회 로직.
        for (Airline a: airlineList) {
            System.out.println("list="+a.getId());
        }

        //만든 dto는 검색조건을 담아 가져오는 객체임. dto 파라미터를 활용해 검색 진행할것.
    }
*/
    /*private List<Airline> searchAirline(AirlineSearchDto airlineSearchDto){
        return queryFactory
                .select(airline)
                .from(airline)
                .join(airline.airlineSeats,airlineSeat).fetchJoin()//.on(airline.id.eq(airlineSeat.airline().id))
                //.where(departureEq(airlineSearchDto.getDeparture())
                 //       .and(distinationEq(airlineSearchDto.getDistination())))
                //.and(airline.depart_date.eq(airlineSearchDto.getDepart_date()))
                // .and(airline.aboveseat.goe(airlineSearchDto.getPeople())))
                .fetch();
    }*/

    private BooleanExpression departureEq(Departure departure){
        return departure != null ? airline.departure.eq(departure) : null;
    }

    private BooleanExpression distinationEq(Distination distination){
        return distination != null ? airline.distination.eq(distination) : null;
    }

    private BooleanExpression departTimeEq(LocalDate depart_date){

        return depart_date != null ? airline.depart_date.goe(depart_date) : null;
    }


    @Test
    public void testCreateAirline(){
        //airlineService.createAirline();
    }
}
