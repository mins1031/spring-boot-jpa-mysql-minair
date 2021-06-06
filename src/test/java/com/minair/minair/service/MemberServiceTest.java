package com.minair.minair.service;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.domain.notEntity.Gender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional(readOnly = true)
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @BeforeEach
    public void before(){

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


}