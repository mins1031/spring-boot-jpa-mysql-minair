package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.dto.member.MemberCreateDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.service.SeatService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;


    @BeforeEach
    public void test(){
        String username = "member";
        String pw = "test";
        String email = "ee@ee";
        LocalDate birth = LocalDate.of(2000,02,22);
        String name_kor = "테스트";
        String name_eng = "test";
        String phone = "010-4533-2222";
        Gender gender = Gender.F;
        String role = "ROLE_MEMBER";
        MemberRole memberRole = MemberRole.ROLE_MEMBER;
        RefreshTokenProperty refreshTokenProperty =
                new RefreshTokenProperty(
                        UUID.randomUUID().toString(),
                        new Date().getTime()
                );
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

        Member member = Member.createMember(memberCreateDto);
        //member.investRole(role);
        member.investMemberRole(memberRole);
        member.issueRefreshToken(refreshTokenProperty);
        memberRepository.save(member);
    }

    @Test
    public void testCheckId(){
        String id = "mi";
        int result = memberRepository.checkId(id);

        assertThat(result).isEqualTo(1);
    }

    @Transactional
    @Test
    public void refreshTest(){

        Member member = memberRepository.findByUsername("member");
        String refreshTokenValue = member.getRefreshToken().getRefreshTokenValue();
        Member member1 = memberRepository.findByRefreshToken(refreshTokenValue);
        System.out.println(member1);
        Assertions.assertThat(member1.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    public void findOne(){
        Member member = memberRepository.findByUsername("member");
        System.out.println(member.getRole());
        Assertions.assertThat(member).isNotNull();
    }
}