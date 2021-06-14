package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.dto.member.MemberCreateDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
@Transactional(readOnly = true)
@Rollback(value = false)
public class CustomReservationRepositoryImplTest {

    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager em;

    @Before
    @Transactional
    public void before(){
        String username = "member";
        String pw = "test";
        String email = "ee@ee";
        LocalDate birth = LocalDate.of(2000,02,22);
        String name_kor = "테스트";
        String name_eng = "test";
        String phone = "010-4533-2222";
        Gender gender = Gender.F;
        MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                .username(username)
                .password(pw)
                .email(email)
                .birth(birth)
                .nameKor(name_kor)
                .nameEng(name_eng)
                .phone(phone)
                .gender(gender)
                .build();

        RefreshTokenProperty refreshTokenProperty =
                new RefreshTokenProperty(
                        UUID.randomUUID().toString(),
                        new Date().getTime()
                );
        Member member = Member.createMember(memberCreateDto);
        MemberRole memberRole = MemberRole.ROLE_MEMBER;
        member.investMemberRole(memberRole);
        RefreshTokenProperty r = new RefreshTokenProperty();
        member.issueRefreshToken(r);
        memberRepository.save(member);

        AirlineGenerateDto airlineGenerateDto1 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,06,03))
                .departTime(LocalTime.of(13,20))
                .reachTime(LocalTime.of(14,00))
                .build();

        Airline airline1 = Airline.createAirline(airlineGenerateDto1);
        Airline airline2 = Airline.createAirline(airlineGenerateDto1);

        airlineRepository.save(airline1);
        airlineRepository.save(airline2);

        Reservation reservation1 = Reservation.createReservation(member,airline1,1,1,2,80000);
        Reservation reservation2 = Reservation.createReservation(member,airline2,1,1,2,80000);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        em.flush();
        em.clear();
    }

    @Test
    @Transactional
    public void deleteMember(){

        String username = "user1";
        boolean result = reservationRepository.findReservationNotOverDate(username);
        assertThat(result).isEqualTo(true);
    }
}