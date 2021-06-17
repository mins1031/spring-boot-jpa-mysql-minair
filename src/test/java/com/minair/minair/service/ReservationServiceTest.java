package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.ReservationGenerateDto;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.dto.member.MemberCreateDto;
import com.minair.minair.domain.dto.reservation.*;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.exception.NotFoundReservations;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@Transactional(readOnly = true)
class ReservationServiceTest {

    @Autowired
    ReservationService reservationService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    EntityManager em;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void beford(){
        Departure departure1 = Departure.ICN;
        Distination distination1 = Distination.JEJU;
        LocalDate depart_date1 = LocalDate.of(2021,04,03);
        LocalTime depart_time1 = LocalTime.of(12,45);
        LocalTime reach_time1 = LocalTime.of(13,55);

        AirlineGenerateDto airlineGenerateDto = AirlineGenerateDto.builder()
                .departure(departure1)
                .distination(distination1)
                .departDate(depart_date1)
                .departTime(depart_time1)
                .reachTime(reach_time1)
                .build();
        Departure departure2 = Departure.JEJU;
        Distination distination2 = Distination.ICN;
        LocalDate depart_date2 = LocalDate.of(2021,04,8);
        LocalTime depart_time2 = LocalTime.of(16,25);
        LocalTime reach_time2 = LocalTime.of(17,35);

        AirlineGenerateDto airlineGenerateDto2 = AirlineGenerateDto.builder()
                .departure(departure2)
                .distination(distination2)
                .departDate(depart_date2)
                .departTime(depart_time2)
                .reachTime(reach_time2)
                .build();
        Airline airline = Airline.createAirline(airlineGenerateDto);
        Airline airline2 = Airline.createAirline(airlineGenerateDto2);

        MemberCreateDto createDto = MemberCreateDto.builder()
                .username("user1")
                .password(passwordEncoder.encode("test"))
                .email("min@min")
                .birth(LocalDate.of(2021,05,30))
                .nameKor("민")
                .nameEng("minyoung")
                .phone("010-2222-2222")
                .gender(Gender.F)
                .build();

        MemberCreateDto createDto2 = MemberCreateDto.builder()
                .username("user2")
                .password(passwordEncoder.encode("test"))
                .email("min2@min")
                .birth(LocalDate.of(2021,05,30))
                .nameKor("민2")
                .nameEng("minyoung2")
                .phone("010-2222-2122")
                .gender(Gender.F)
                .build();

        RefreshTokenProperty refreshTokenProperty =
                new RefreshTokenProperty(null,0);
        MemberRole memberRole = MemberRole.ROLE_MEMBER;
        Member member2 = Member.createMember(createDto2,memberRole);
        Member member = Member.createMember(createDto,memberRole);
        member.issueRefreshToken(refreshTokenProperty);
        member2.issueRefreshToken(refreshTokenProperty);

        airlineRepository.save(airline);
        airlineRepository.save(airline2);
        memberRepository.save(member);

        Long goAirId = 4L;
        Long backAirId = 5L;
        String username2 = "test";
        int adult = 2;
        int child = 1;
        int totalPerson = adult + child;
        int totalPrice = 260000;
        ReservationGenerateDto reservationGenerateDto1 =
                ReservationGenerateDto.builder()
                        .member(member)
                        .airline(airline)
                        .adultCount(adult)
                        .childCount(child)
                        .totalPerson(totalPerson)
                        .totalPrice(totalPrice)
                        .build();
        ReservationGenerateDto reservationGenerateDto2 =
                ReservationGenerateDto.builder()
                        .member(member)
                        .airline(airline2)
                        .adultCount(adult)
                        .childCount(child)
                        .totalPerson(totalPerson)
                        .totalPrice(totalPrice)
                        .build();
        Reservation reservation = Reservation.createReservation(reservationGenerateDto1);
        Reservation reservation2 = Reservation.createReservation(reservationGenerateDto2);
        Reservation reservation3 = Reservation.createReservation(reservationGenerateDto1);
        Reservation reservation4 = Reservation.createReservation(reservationGenerateDto2);
        Reservation reservation5 = Reservation.createReservation(reservationGenerateDto1);
        Reservation reservation6 = Reservation.createReservation(reservationGenerateDto2);
        Reservation reservation7 = Reservation.createReservation(reservationGenerateDto1);
        Reservation reservation8 = Reservation.createReservation(reservationGenerateDto2);
        Reservation reservation9 = Reservation.createReservation(reservationGenerateDto1);
        Reservation reservation10 = Reservation.createReservation(reservationGenerateDto2);
        Reservation reservation11 = Reservation.createReservation(reservationGenerateDto1);

        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
        reservationRepository.save(reservation4);
        reservationRepository.save(reservation5);
        reservationRepository.save(reservation6);
        reservationRepository.save(reservation7);
        reservationRepository.save(reservation8);
        reservationRepository.save(reservation9);
        reservationRepository.save(reservation10);
        reservationRepository.save(reservation11);

    }

    @Test
    @DisplayName("예약 테스트")
    void reservationTest() {
        //Given
        Optional<Airline> goOp = airlineRepository.findById(1L);
        Airline goAir = goOp.get();

        Optional<Airline> backOp = airlineRepository.findById(2L);
        Airline backAir = backOp.get();
        String username = "user1";
        int adult = 2;
        int child = 1;
        int totalPerson = adult + child;
        int totalPrice = 260000;
        Member member = memberRepository.findByUsername(username);

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setGoAirId(goAir.getId());
        reservationDto.setBackAirId(backAir.getId());
        reservationDto.setUsername(member.getUsername());
        reservationDto.setAdultCount(adult);
        reservationDto.setChildCount(child);
        reservationDto.setTotalPerson(totalPerson);
        reservationDto.setTotalPrice(totalPrice);
        //When
        List<ReservationResultDto> resultDtos = reservationService.reservation(reservationDto);
        //Then
        assertEquals(resultDtos.get(0).getAirlineId(), goAir.getId());
        assertEquals(resultDtos.get(1).getAirlineId(), backAir.getId());
    }

    @Test
    @DisplayName("파리미터가 null인 경우 예약 테스트")
    void parameterNullReservationTest() {
        //Given
        Optional<Airline> goOp = airlineRepository.findById(1L);
        Airline goAir = goOp.get();

        Optional<Airline> backOp = airlineRepository.findById(2L);
        Airline backAir = backOp.get();
        String username = "user1";
        int adult = 2;
        int child = 1;
        int totalPerson = adult + child;
        int totalPrice = 260000;
        Member member = memberRepository.findByUsername(username);

        ReservationDto reservationDto = null;
        //When & Then
        assertThrows(RequestNullException.class, () -> reservationService.reservation(reservationDto));
    }

    @Test
    @DisplayName("회원 예약목록 조회 테스트")
    void findReservationsTest() {
        //Given
        String username = "user1";
        int pageNum = 1;
        //When
        ReservationsResultDto reservations = reservationService.findReservations(username, pageNum);
        //Then
        List<ReservationResultApiDto> o = (List<ReservationResultApiDto>) reservations.getReservations();
        System.out.println(o.get(0));
        for (ReservationResultApiDto r: o) {
            assertEquals(r.getUsername(),username);
        }
        assertEquals(reservations.getReservations().getClass(), ArrayList.class);
    }

    @Test
    @DisplayName("회원의 예약목록이 없는 경우 테스트")
    void noReservationFoundTest() {
        //Given
        String username = "user2";
        int pageNum = 1;
        //When & Then
        assertThrows(NotFoundReservations.class,
                () -> reservationService.findReservations(username, pageNum));
    }

    @Test
    @DisplayName("예약에 대한 상세조회 테스트")
    void findOneReservationTest() {
        //Given
        String username = "user1";
        Long reservationId = 1L;
        //When
        ReservationDetailInfoDto oneReservation = reservationService.findOneReservation(reservationId);
        //Then
        assertEquals(oneReservation.getUsername(), username);
    }

    @Test
    @DisplayName("없는 예약 조회 테스트")
    void NoReservationTest() {
        //Given
        String username = "user1";
        Long reservationId = 25L;
        //When & Then
        assertEquals(RuntimeException.class, reservationService.findOneReservation(reservationId));
    }
//========06/17 리펙토링 + 테스트 위 테스트 실패=====================
    @Test
    void checkSeat() {
        //Given
        //When
        //Then
    }

    @Test
    void findAll() {
        //Given
        //When
        //Then
    }

    @Test
    void remove() {
        //Given
        //When
        //Then
    }
}