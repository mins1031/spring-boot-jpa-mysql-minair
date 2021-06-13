package com.minair.minair.service;

import com.minair.minair.common.TestDescription;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.dto.member.LoginRequestDto;
import com.minair.minair.domain.dto.member.LoginServiceDto;
import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.MemberRepository;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional(readOnly = true)
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    public void before(){
        Member member = Member.joinMember("user1",passwordEncoder.encode("alsdud"),"min@min",
                LocalDate.of(2021,05,30),"민","min","010-2222-2222",
                Gender.F);
        Member member2 = Member.joinMember("user2",passwordEncoder.encode("jae"),"jae@jae",
                LocalDate.of(2021,05,30),"재","jae","010-2222-2222",
                Gender.M);
        MemberRole memberRole = MemberRole.ROLE_ADMIN;
        member.investMemberRole(memberRole);
        MemberRole memberRole2 = MemberRole.ROLE_MEMBER;
        member2.investMemberRole(memberRole2);
        RefreshTokenProperty r = new RefreshTokenProperty();
        member.issueRefreshToken(r);
        member2.issueRefreshToken(r);

        memberRepository.save(member);
    }


    @Test
    @Rollback(value = false)
    public void joinTest(){

        String username = "test";
        String password = "test";
        String email = "test@test";
        LocalDate birth = LocalDate.of(2000,02,20);
        String name_kor = "민영";
        String name_eng = "minyoung";
        String phone = "010-1111-2222";
        Gender gender = Gender.M;

        MemberJoinDto memberJoinDto = new MemberJoinDto(username,password,email,
                birth,name_kor,name_eng,phone,gender);


        memberService.join(memberJoinDto);

        Optional<Member> optionalMember = memberService.findById(1L);
        Member member = optionalMember.get();

        assertThat(memberJoinDto.getUsername()).isEqualTo(member.getUsername());
    }

    @Test
    @TestDescription("일반 로그인 테스트")
    public void loginTest(){
        //Given
        String username = "user1";
        String password = "alsdud";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username,password);
        //When
        LoginServiceDto loginResult = memberService.login(loginRequestDto);
        //Then
        assertThat(loginResult.isPassLogin()).isEqualTo(true);
    }

    @Test
    @TestDescription("로그인시 id값이 잘못된 경우 테스트")
    public void wrongIdLoginTest(){
        //Given
        String username = "user";
        String password = "alsdud";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username,password);
        //When
        LoginServiceDto loginResult = memberService.login(loginRequestDto);
        //Then
        assertThat(loginResult.isIdNotMatch()).isEqualTo(true);
    }

    @Test
    @TestDescription("로그인시 pwd값이 잘못된 경우 테스트")
    public void wrongPwdLoginTest(){
        //Given
        String username = "user1";
        String password = "alsdu";
        LoginRequestDto loginRequestDto = new LoginRequestDto(username,password);
        //When
        LoginServiceDto loginResult = memberService.login(loginRequestDto);
        //Then
        assertThat(loginResult.isWrongPwd()).isEqualTo(true);
    }

}