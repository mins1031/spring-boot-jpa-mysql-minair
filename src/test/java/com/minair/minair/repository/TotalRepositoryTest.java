package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.AirlineSeat;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
public class TotalRepositoryTest {

    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    AirlinSeatRepository airlinSeatRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    ReservationRepository repository;
    @Autowired
    EntityManager em;


    @Test
    public void testAirline(){

        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i < 10; i++) {
            Seat seat = Seat.createSeat("A"+i);
            //seats.add(seat);
            seatRepository.save(seat);
        }
        em.flush();
        em.clear();
        //원래는 db에 좌석들의 값이 있어야 하지만 초기 테스트이므로 직접 생성해 넣어줌

        List<Seat> seatList = seatRepository.findAll();
        AirlineGenerateDto airlineGenerateDto = AirlineGenerateDto.builder()
                .departure(Departure.ICN)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,04,03))
                .departTime(LocalTime.of(12,45))
                .reachTime(LocalTime.of(13,55))
                .build();
        Airline airline = Airline.createAirline(airlineGenerateDto);


        //우선 항공권을 추가하려면 각 좌석에 좌석 정보를 넣어야 하고 좌석정보는 좌석의 정보가 있어야하기에
        //좌석생성 -> 좌석과항공의 정보 생성 -> 항공권 생성시 정보 넣어줌 이 식으로 진행 되어야 한다.
        //다만 항공 좌석의 정보가 생성되어 항공에 입력되야하는데 항공좌석정보생성은 항공에 대한 정보가 필요함,즉
        //Airline의 createAirline은 좌석정보를 일단 보류하고 좌석정보보다 먼저 생성되어야함.
        //해서 좌석의 갯수만큼 시트 정보가 생성되어 항공정보의 좌석정보 리스트에 입력되야함

        List<AirlineSeat> airlineSeat = airlinSeatRepository.findAll();

        //airline.setSeat(airlineSeat);

        airlineRepository.save(airline);
        em.flush();
        em.clear();

        Optional<Airline> optionalAirline = airlineRepository.findById(airline.getId());
        Airline findAirline = optionalAirline.get();
        assertThat(findAirline.getId()).isEqualTo(airline.getId());
        //조회 확인

        Departure departure1 = Departure.DAE;
        //findAirline.updateDeparture(departure1);
        em.flush();
        em.clear();

        assertThat(findAirline.getDeparture()).isEqualTo(departure1);
        //수정 확인

        //저장, 업데이트 조회 잘됨. 위 로직 토대로 항공권 생성 서비스 로직에 구현하면 됨.
    }

    @Test
    public void testSeat(){
        Seat seatA = Seat.createSeat("A1");
        Seat seatB = Seat.createSeat("A2");
        seatRepository.save(seatA);
        seatRepository.save(seatB);

        em.flush();
        em.clear();
        Optional<Seat> optionalSeat = seatRepository.findById(seatA.getId());
        Seat findSeat = optionalSeat.get();

        assertThat(findSeat.getSeatName()).isEqualTo("A1");
        //저장 조회 확인

        seatRepository.delete(seatB);
        //1) db에 seatB 입력된것 확인.삭제 테스트 해볼것
        //2) 삭제 확인.
    }

}
