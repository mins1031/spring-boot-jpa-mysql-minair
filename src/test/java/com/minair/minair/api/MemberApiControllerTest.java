package com.minair.minair.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.member.LoginRequestDto;
import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.domain.dto.member.MemberModifyDto;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import com.minair.minair.service.MemberService;
import com.minair.minair.testconfig.RestDocsConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class MemberApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Before
    public void before(){

        Member member = Member.joinMember("user1",passwordEncoder.encode("alsdud"),"min@min",
                LocalDate.of(2021,05,30),"민","min","010-2222-2222",
                Gender.F);
        Member member2 = Member.joinMember("user2",passwordEncoder.encode("jae"),"jae@jae",
                LocalDate.of(2021,05,30),"재","jae","010-2222-2222",
                Gender.M);
        member.investRole("ROLE_MEMBER,ROLE_ADMIN");
        member2.investRole("ROLE_MEMBER");
        RefreshTokenProperty r = new RefreshTokenProperty();
        member.issueRefreshToken(r);
        member2.issueRefreshToken(r);

        memberRepository.save(member);
    }

    @Test
    public void join() throws Exception {
        //Given
        String username = "member";
        String password = "alsdud";
        String email = "mem@mem";
        String name_kor = "멤버";
        String name_eng = "member";
        String phone = "010-1111-0000";
        Gender gender = Gender.M;
        LocalDate birth = LocalDate.of(2020,8,19);
        MemberJoinDto memberJoinDto =
                new MemberJoinDto(username,password,email,birth,name_kor,name_eng,phone,gender);
        //When & Then
        this.mockMvc.perform(post("/api/member/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(memberJoinDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("birth").exists())
                .andExpect(jsonPath("name_kor").exists())
                .andExpect(jsonPath("name_eng").exists())
                .andExpect(jsonPath("phone").exists())
                .andExpect(jsonPath("gender").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
        .andDo(document("join",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api")
                ),
                requestFields(
                        fieldWithPath("username").description("회원가입 유저 id값"),
                        fieldWithPath("password").description("회원가입 유저 비밀번호값"),
                        fieldWithPath("email").description("회원가입 유저 이메일 값"),
                        fieldWithPath("birth").description("회원가입 유저 생년월일 값"),
                        fieldWithPath("name_kor").description("회원가입 유저 이름 값"),
                        fieldWithPath("name_eng").description("회원가입 유저 영어이름 값"),
                        fieldWithPath("phone").description("회원가입 유저 핸드폰 번호값"),
                        fieldWithPath("gender").description("회원가입 유저 성별값")
                ),
                responseFields(
                        fieldWithPath("username").description("회원가입 유저 id 값"),
                        fieldWithPath("email").description("회원가입 유저 이메일 값"),
                        fieldWithPath("birth").description("회원가입 유저 생년월일 값"),
                        fieldWithPath("name_kor").description("회원가입 유저 이름 값"),
                        fieldWithPath("name_eng").description("회원가입 유저 영어이름 값"),
                        fieldWithPath("phone").description("회원가입 유저 핸드폰 번호 값"),
                        fieldWithPath("gender").description("회원가입 유저 성별 값"),
                        fieldWithPath("regDate").description("회원가입 유저 가입일시 값"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.index.href").description("index href"),
                        fieldWithPath("_links.profile.href").description("document href for explain this api")
                )
        ))
        ;
    }

    @Test
    public void checkId() throws Exception {
        String id = "user1";
        String newId = "mmm";

        this.mockMvc.perform(get("/api/member/checkId/{id}",newId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("YES"))
        ;
    }

    @Test
    public void login() throws Exception {
        String username = "user1";
        String password = "alsdud";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username,password);
        this.mockMvc.perform(post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
        .andDo(document("login",
                requestFields(
                        fieldWithPath("username").description("사용자 id 값"),
                        fieldWithPath("password").description("사용자 password 값")
                ),
                responseFields(
                        fieldWithPath("token").description("엑세스 토큰 값")
                )
                ))
        ;
    }

    @Test
    public void logout() throws Exception {
        //Given
        RefreshTokenProperty refreshTokenProperty =
                new RefreshTokenProperty(
                        UUID.randomUUID().toString(),new Date().getTime()
                );
        System.out.println(refreshTokenProperty.getRefreshTokenValue());
        System.out.println(refreshTokenProperty.getRefreshTokenExpirationPeriod());

        Member user1 = memberRepository.findByUsername("user1");
        user1.issueRefreshToken(refreshTokenProperty);
        memberRepository.save(user1);

        TokenDto accessToken = TokenDto.builder()
                .token(jwtTokenProvider.createToken(user1.getUsername(),user1.getRoleList()))
                .build();
        TokenDto refreshToken = TokenDto.builder()
                .token(jwtTokenProvider.createRefreshToken(refreshTokenProperty))
                .build();
        //When & Then
        this.mockMvc.perform(get("/api/member/logout/{token}",
                refreshToken.getToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
        .header("Authorization",accessToken.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
        ;

    }

    @Test
    public void findMember() throws Exception {
        //Given
        String username = "user1";
        Member member = memberRepository.findByUsername(username);
        TokenDto tokenDto = new TokenDto(jwtTokenProvider.createToken(
                member.getUsername(),member.getRoleList()));
        //When & Then
        this.mockMvc.perform(get("/api/member/{username}", username)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("Authorization",tokenDto.getToken()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("birth").exists())
                .andExpect(jsonPath("name_kor").exists())
                .andExpect(jsonPath("name_eng").exists())
                .andExpect(jsonPath("phone").exists())
                .andExpect(jsonPath("gender").exists())
                .andExpect(jsonPath("regDate").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
        .andDo(document("member_info",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api")
                ),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰 accessToken 식별 값")
                ),
                responseFields(
                        fieldWithPath("username").description("회원 id 값"),
                        fieldWithPath("email").description("회원 email 값"),
                        fieldWithPath("birth").description("회원 생년월일 값"),
                        fieldWithPath("name_kor").description("회원 한국이름 값"),
                        fieldWithPath("name_eng").description("회원 영어이름 값"),
                        fieldWithPath("phone").description("회원 휴대번호 값"),
                        fieldWithPath("gender").description("회원 성별 값"),
                        fieldWithPath("regDate").description("회원 등록일 값"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.index.href").description("index href"),
                        fieldWithPath("_links.profile.href").description("document href for explain this api")
                )
                ))
        ;
    }

    @Test
    public void modifyMember() throws Exception {
        //Given
        String username = "user1";
        Member member = memberRepository.findByUsername(username);
        System.out.println(member);
        System.out.println(member.getName_eng());
        String updateNameEng = "kkkkkk";
        MemberModifyDto memberModifyDto = MemberModifyDto.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .birth(member.getBirth())
                .name_kor(member.getName_kor())
                .name_eng(updateNameEng)
                .phone(member.getPhone())
                .gender(member.getGender())
                .build();
        TokenDto tokenDto = TokenDto.builder()
                .token(jwtTokenProvider.createToken(member.getUsername(),member.getRoleList()))
                .build();

        //When & Then
        this.mockMvc.perform(put("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("Authorization",tokenDto.getToken())
                .content(objectMapper.writeValueAsString(memberModifyDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("birth").exists())
                .andExpect(jsonPath("name_kor").exists())
                .andExpect(jsonPath("name_eng").exists())
                .andExpect(jsonPath("phone").exists())
                .andExpect(jsonPath("gender").exists())
                .andExpect(jsonPath("regDate").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andExpect(jsonPath("_links.member-info.href").exists())
        .andDo(document("member-modify",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api"),
                        linkWithRel("member-info").description("회원 정보 link")
                ),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰 accessToken 식별 값")
                ),
                requestFields(
                        fieldWithPath("username").description("회원 id 값"),
                        fieldWithPath("email").description("회원 email 값"),
                        fieldWithPath("birth").description("회원 생년월일 값"),
                        fieldWithPath("name_kor").description("회원 한국이름 값"),
                        fieldWithPath("name_eng").description("회원 영어이름 값"),
                        fieldWithPath("phone").description("회원 휴대번호 값"),
                        fieldWithPath("gender").description("회원 성별 값")
                ),
                responseFields(
                        fieldWithPath("username").description("회원 id 값"),
                        fieldWithPath("email").description("회원 email 값"),
                        fieldWithPath("birth").description("회원 생년월일 값"),
                        fieldWithPath("name_kor").description("회원 한국이름 값"),
                        fieldWithPath("name_eng").description("회원 영어이름 값"),
                        fieldWithPath("phone").description("회원 휴대번호 값"),
                        fieldWithPath("gender").description("회원 성별 값"),
                        fieldWithPath("regDate").description("회원 등록일 값"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.index.href").description("index href"),
                        fieldWithPath("_links.profile.href").description("document href for explain this api"),
                        fieldWithPath("_links.member-info.href").description("회원 정보 link"),
                        fieldWithPath("_links.member-info.templated").description("templated")
                )
                ))
                ;
    }

    @Test
    public void findAllMember() throws Exception {
        //Given
        int pageNum = 1;
        generateMember(15);
        ForFindPagingDto forFindPagingDto = new ForFindPagingDto();
        forFindPagingDto.setPageNum(pageNum);

        String findMemberName = "user1";
        Member member = memberRepository.findByUsername(findMemberName);
        TokenDto tokenDto = TokenDto.builder()
                .token(jwtTokenProvider.createToken(member.getUsername(),member.getRoleList()))
                .build();

        //When & Then
        this.mockMvc.perform(get("/api/member")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("Authorization",tokenDto.getToken())
                .content(objectMapper.writeValueAsString(forFindPagingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("memberList").exists())
                .andExpect(jsonPath("pageDto").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andExpect(jsonPath("_links.member-info.href").exists())
        .andDo(document("allmember-find",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api"),
                        linkWithRel("member-info").description("회원 정보 link")
                ),
                requestHeaders(
                        headerWithName("Authorization").description("JWT 토큰 accessToken 식별 값")
                ),
                requestFields(
                        fieldWithPath("pageNum").description("페이징을 위한 페이지 값"),
                        fieldWithPath("username").description("회원을 식별할경우 필요한 값. 필수 x")
                ),
                relaxedResponseFields(
                        fieldWithPath("memberList").description("페이징 처리된 10개의 회원리스트 값"),
                        fieldWithPath("memberList[0].id").description("각 회원의 회원코드 값"),
                        fieldWithPath("memberList[0].username").description("각 회원의 id 값"),
                        fieldWithPath("memberList[0].email").description("각 회원의 email 값"),
                        fieldWithPath("memberList[0].name_kor").description("각 회원의 한글이름 값"),
                        fieldWithPath("memberList[0].name_eng").description("각 회원의 영어이름 값"),
                        fieldWithPath("memberList[0].phone").description("각 회원의 휴대번호 값"),
                        fieldWithPath("memberList[0].gender").description("각 회원의 성별 값"),
                        fieldWithPath("memberList[0].regDate").description("각 회원의 가입일 값"),
                        fieldWithPath("pageDto").description("페이징과 관련된 정보 객체"),
                        fieldWithPath("pageDto.startPage").description("첫번째 페이지 값"),
                        fieldWithPath("pageDto.endPage").description("마지막 페이지 값"),
                        fieldWithPath("pageDto.prev").description("이전버튼. 현 페이지의 startPage가 11이상인경우 true"),
                        fieldWithPath("pageDto.next").description("다음버튼. 페이지의 갯수가 10개 이상인 경우 true"),
                        fieldWithPath("pageDto.total").description("모든 데이터 갯수 값"),
                        fieldWithPath("pageDto.realEndPage").description("최종 endPage값"),
                        fieldWithPath("pageDto.offset").description("현재 페이지 값"),
                        fieldWithPath("pageDto.limit").description("한번 조회시 가져오는 데이터 수 값"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.index.href").description("index href"),
                        fieldWithPath("_links.profile.href").description("document href for explain this api"),
                        fieldWithPath("_links.member-info.href").description("회원 정보 link"),
                        fieldWithPath("_links.member-info.templated").description("templated")
                )
                ))
        ;
    }

    @Autowired
    MemberService memberService;

    public void generateMember(int count){
        for (int i = 0; i<count;i++) {
            MemberJoinDto member = new MemberJoinDto("testId"+i,"testPw"+i,
                    "testEmail"+i,LocalDate.of(2021,06,07),
                    "testNameKor"+i, "testNameEng"+i,
                    "testPhone"+i,Gender.M);

            memberService.join(member);
        }
    }


    @Test
    public void checkAdmin() {
    }

    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    ReservationRepository reservationRepository;

    @Test
    public void removeMember() throws Exception {
        //Given
        Airline airline1 = Airline.createAirline(Departure.JEJU, Distination.BUS,
                LocalDate.of(2021,05,20), LocalTime.of(13,55),
                LocalTime.of(14,20),18);
        Airline airline2 = Airline.createAirline(Departure.JEJU,Distination.BUS,
                LocalDate.of(2021,05,20), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Member member = memberRepository.findByUsername("user1");
        Reservation reservation =
                Reservation.createReservation(member,airline1,1,1,2,80000);
        Reservation reservation2 =
                Reservation.createReservation(member,airline2,1,1,2,80000);

        airlineRepository.save(airline1);
        airlineRepository.save(airline2);
        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);

        String username = member.getUsername();
        //When & Then
        this.mockMvc.perform(delete("/api/member/{username}",username))
                .andDo(print())
                .andExpect(status().isOk())
        .andExpect(content().string("false"))
                ;
    }
}