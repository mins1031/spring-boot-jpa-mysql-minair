package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.ReservationDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional(readOnly = true)
@Rollback(value = false)
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

    @BeforeEach
    public void beford(){
        Departure departure1 = Departure.ICN;
        Distination distination1 = Distination.JEJU;
        LocalDate depart_date1 = LocalDate.of(2021,04,03);
        LocalTime depart_time1 = LocalTime.of(12,45);
        LocalTime reach_time1 = LocalTime.of(13,55);

        Airline airline = Airline.createAirline(departure1,distination1,depart_date1,
                depart_time1,reach_time1,18);

        Departure departure2 = Departure.JEJU;
        Distination distination2 = Distination.ICN;
        LocalDate depart_date2 = LocalDate.of(2021,04,8);
        LocalTime depart_time2 = LocalTime.of(16,25);
        LocalTime reach_time2 = LocalTime.of(17,35);

        Airline airline2 = Airline.createAirline(departure2,distination2,depart_date2,
                depart_time2,reach_time2,18);


        String username = "member";
        String password = "test";
        String email = "test@test";
        LocalDate birth = LocalDate.of(2000,02,20);
        String name_kor = "민영";
        String name_eng = "minyoung";
        String phone = "010-1111-2222";
        Gender gender = Gender.M;

        String role = "ROLE_MEMBER";

        RefreshTokenProperty refreshTokenProperty =
                new RefreshTokenProperty(null,0);
        Member member = Member.joinMember(username,password,email,birth,name_kor,name_eng,
                phone,gender);

        member.investRole(role);
        member.issueRefreshToken(refreshTokenProperty);
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

        Reservation reservation = Reservation.createReservation(member,airline,
                adult,child,totalPerson,totalPrice);
        Reservation reservation2 = Reservation.createReservation(member,airline2,
                adult,child,totalPerson,totalPrice);
        Reservation reservation3 = Reservation.createReservation(member,airline,
                adult,child,totalPerson,totalPrice);
        Reservation reservation4 = Reservation.createReservation(member,airline2,
                adult,child,totalPerson,totalPrice);
        Reservation reservation5 = Reservation.createReservation(member,airline,
                adult,child,totalPerson,totalPrice);
        Reservation reservation6 = Reservation.createReservation(member,airline2,
                adult,child,totalPerson,totalPrice);
        Reservation reservation7 = Reservation.createReservation(member,airline,
                adult,child,totalPerson,totalPrice);
        Reservation reservation8 = Reservation.createReservation(member,airline2,
                adult,child,totalPerson,totalPrice);
        Reservation reservation9 = Reservation.createReservation(member,airline,
                adult,child,totalPerson,totalPrice);
        Reservation reservation10 = Reservation.createReservation(member,airline2,
                adult,child,totalPerson,totalPrice);
        Reservation reservation11 = Reservation.createReservation(member,airline,
                adult,child,totalPerson,totalPrice);

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
    public void listTest(){
        String username = "member";
        /*List<Reservation> reservation = reservationService.findReservation(username);
        System.out.println(reservation.get(0).getAirline().getDeparture());
        System.out.println(reservation.get(0).getAirline().getDistination());
        System.out.println(reservation.get(1).getAirline().getDeparture());
        System.out.println(reservation.get(1).getAirline().getDistination());
*/
    }

    @Test
    public void reservationsPagingTest(){
        String username = "member";
        int offset1 = 1;
        int offset2 = 2;

        Page<Reservation> reservations = reservationService.findReservation(username, offset2);
        for (Reservation r: reservations.getContent()) {
            System.out.println("page="+r.getId());
        }
        System.out.println("전체 데이터 갯수"+reservations.getTotalElements());
        System.out.println("전체 페이지 번호"+reservations.getTotalPages());
        System.out.println("??"+reservations.getPageable());
        System.out.println("전체 페이지 번호"+reservations.getNumberOfElements());
        //System.out.println("전체 페이지 번호"+reservations.);

        System.out.println("페이지 번호"+reservations.getNumber());
        System.out.println("첫 번째 항목인가?"+reservations.isFirst());
        System.out.println("이전 페이지항목들이 있는가?"+reservations.hasPrevious());
        System.out.println("다음 페이지 항목들이 있는가?"+reservations.hasNext());
        System.out.println("첫번째 항목?"+reservations.isFirst());


        //System.out.println(reservations.getContent());
    }

    @Test
    public void reserveTest(){
        Long goAirId = 4L;
        Long backAirId = 5L;
        String username = "member";
        int adult = 2;
        int child = 1;
        int totalPerson = adult + child;
        int totalPrice = 260000;

        ReservationDto reservationDto =
                new ReservationDto(goAirId,backAirId,username,adult,child
                ,totalPerson,totalPrice);

        //Optional<Member> testMem = memberRepository.findById(8L);
        //assertThat(testMem.isPresent()).isTrue();

        Optional<Airline> first = airlineRepository.findById(1L);
        Optional<Airline> second = airlineRepository.findById(1L);
        assertThat(first.isPresent()).isTrue();
        assertThat(second.isPresent()).isTrue();
        //Airline goAir = first.get();
        //Airline backAir = second.get();
        System.out.println(first.isPresent());
        System.out.println(second.isPresent());

        //List<Reservation> reservationList = reservationService.reservation(reservationDto);

        /*for (Reservation r: reservationList) {
            System.out.println(r);
        }*/
    }
}