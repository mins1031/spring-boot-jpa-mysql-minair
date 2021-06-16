package com.minair.minair.service;

import com.minair.minair.common.TestDescription;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.dto.member.*;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.jwt.TokenProperty;
import com.minair.minair.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional(readOnly = true)
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void before(){
        MemberCreateDto createDto = MemberCreateDto.builder()
                .username("user1")
                .password(passwordEncoder.encode("alsdud"))
                .email("min@min")
                .birth(LocalDate.of(2021,05,30))
                .nameKor("민")
                .nameEng("min")
                .phone("010-2222-2222")
                .gender(Gender.F)
                .build();
        MemberRole memberRole = MemberRole.ROLE_ADMIN;
        Member member = Member.createMember(createDto,memberRole);
        RefreshTokenProperty r = new RefreshTokenProperty();
        member.issueRefreshToken(r);

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


        MemberInfoDto join = memberService.join(memberJoinDto);
        assertEquals(join.getUsername(), memberJoinDto.getUsername());
    }

    @Test
    @TestDescription("admin계정 생성 테스트")
    public void adminJoinTest(){

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


        MemberInfoDto join = memberService.joinAdmin(memberJoinDto);
        assertEquals(join.getUsername(), memberJoinDto.getUsername());
    }

    @Test
    @TestDescription("회원가입 파라미터 null인경우 테스트")
    public void nullRequestJoinTest(){
        //Given
        String username = "test";
        String password = "test";
        String email = "test@test";
        LocalDate birth = LocalDate.of(2000,02,20);
        String name_kor = "민영";
        String name_eng = null;
        String phone = "010-1111-2222";
        Gender gender = Gender.M;

        MemberJoinDto memberJoinDto = new MemberJoinDto(username,password,email,
                birth,name_kor,name_eng,phone,gender);
        //When
        MemberInfoDto join = memberService.join(memberJoinDto);
        //Then
        assertEquals(join.getUsername(), memberJoinDto.getUsername());
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
        assertEquals(loginResult.isPassLogin(), true);
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
        assertEquals(loginResult.isIdNotMatch(), true);
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
        assertEquals(loginResult.isWrongPwd(), true);
    }

    @Transactional
    @Rollback(value = false)
    protected void generateMember(int count){
        for (int i = 0; i < count; i++){
            String username = "test"+i;
            String password = "test"+i;
            String email = "test@test"+i;
            LocalDate birth = LocalDate.of(2000,02,20);
            String name_kor = "test"+i;
            String name_eng = "test"+i;
            String phone = "010-1111-2222";
            Gender gender = Gender.M;
            MemberCreateDto memberCreateDto = MemberCreateDto.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .birth(birth)
                    .nameKor(name_kor)
                    .nameEng(name_eng)
                    .phone(phone)
                    .gender(gender)
                    .build();
            MemberRole memberRole = MemberRole.ROLE_MEMBER;
            Member member = Member.createMember(memberCreateDto,memberRole);

            RefreshTokenProperty r = new RefreshTokenProperty();
            member.issueRefreshToken(r);
            memberRepository.save(member);
        }
    }

    @Test
    @TestDescription("모든 사용자 조회 테스트")
    public void findAllMemberTest(){
        //Given
        int pageNum = 2;
        generateMember(15);
        //When
        Page<Member> byAll = memberService.findByAll(pageNum);
        //Then
        for (Member m:byAll.getContent()) {
            System.out.println("유저의 이름"+m.getNameKor());
        }
    }

    @Test
    @DisplayName("pageNum값 0 인경우 테스트")
    @Disabled
    public void pageNumZeroTest(){
        //Given
        int pageNum = 0;
        //When
        memberService.findByAll(pageNum);
        //Then
        Assert.fail("IllegalArgumentException이 발생해야 한다.");
    }

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("멤버 업데이트 테스트")
    public void updateMemberTest(){
        //Given
        Member user1 = memberRepository.findByUsername("user1");
        String beforeKorName = user1.getNameKor();
        String updateKorName = "update KorName!";

        MemberModifyDto memberModifyDto = MemberModifyDto.builder()
                .name_kor(updateKorName)
                .build();
        //When
        user1.updateMember(memberModifyDto);
        em.flush();
        em.clear();
        //Then
        Member updatedMember = memberRepository.findByUsername("user1");
        assertEquals(updatedMember.getNameKor(), updateKorName);
        assertNotEquals(updatedMember.getNameKor(),beforeKorName);
    }

    @Test
    @DisplayName("로그인시 리프레시 토큰 객체 생성")
    public void issueRefreshTokenObjectTest(){
        //Given
        String username = "user1";
        //When
        TokenDto tokenDto = memberService.issueRefreshToken(username);
        //Then
        assertNotNull(tokenDto);
        Member findMember = memberRepository.findByUsername(username);
        assertEquals(jwtTokenProvider.getMemberName(tokenDto.getToken()),
                findMember.getRefreshToken().getRefreshTokenValue());
    }

    @Test
    @DisplayName("리프레시 토큰 기한 만료시 테스트-> TokenApiController에 작성할것.")
    @Disabled
    public void refreshTokenValidateTest(){
        //Given
        TokenProperty accessTokenProperty = new TokenProperty();
        RefreshTokenProperty r = new RefreshTokenProperty(UUID.randomUUID().toString(),
                -50);
        String username = "user1";
        Member beforeLoginMember = memberRepository.findByUsername(username);
        beforeLoginMember.issueRefreshToken(r);
        em.flush();
        em.clear();
        Member loginMember = memberRepository.findByUsername(username);
        //When
        //Then
        //assertEquals(logout,false);
    }

    @Test
    @DisplayName("로그아웃 테스트")
    public void logoutTest(){
        //Given
        String username = "user1";
        Member beforeLoginMember = memberRepository.findByUsername(username);
        RefreshTokenProperty refreshTokenProperty = new RefreshTokenProperty(UUID.randomUUID().toString(),
                new TokenProperty().getAccessTokenValidTime());
        beforeLoginMember.issueRefreshToken(refreshTokenProperty);
        em.flush();
        em.clear();
        Member loginMember = memberRepository.findByUsername(username);
        assertEquals(loginMember.getRefreshToken().getRefreshTokenValue(),
                refreshTokenProperty.getRefreshTokenValue());
        //When
        boolean logout = memberService.logout(username);
        //Then
        assertEquals(loginMember.getRefreshToken().getRefreshTokenValue(), null);
    }

}