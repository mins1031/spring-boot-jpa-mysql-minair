package com.minair.minair.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.member.MemberCreateDto;
import com.minair.minair.domain.dto.reservation.ReservationRemoveDto;
import com.minair.minair.domain.dto.reservation.CheckInRegDto;
import com.minair.minair.domain.dto.reservation.ReservationDto;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import com.minair.minair.service.AirlineService;
import com.minair.minair.service.SeatService;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class ReservationApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AirlineRepository airlineRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    SeatService seatService;

    @Autowired
    AirlineService airlineService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;
    @Before
    public void before(){
        AirlineGenerateDto airlineGenerateDto1 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.BUS)
                .departDate(LocalDate.of(2021,05,20))
                .departTime(LocalTime.of(13,50))
                .reachTime(LocalTime.of(14,30))
                .build();
        AirlineGenerateDto airlineGenerateDto2 = AirlineGenerateDto.builder()
                .departure(Departure.BUS)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(13,50))
                .reachTime(LocalTime.of(14,30))
                .build();

        Airline goAir = Airline.createAirline(airlineGenerateDto1);
        Airline backAir = Airline.createAirline(airlineGenerateDto2);

        airlineService.createAirline(goAir);
        airlineService.createAirline(backAir);

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

        String username = "user1";
        int adult = 1;
        int child = 1;
        int totalPer = 2;
        int totalPri = 80000;

        Reservation reservation
                = Reservation.createReservation(member,goAir,adult,child,adult+child,totalPri);
        Reservation reservation2
                = Reservation.createReservation(member,backAir,adult,child,adult+child,totalPri);

        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);
    }

    @Test
    public void newTest() throws Exception {
        AirlineGenerateDto airlineGenerateDto1 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.BUS)
                .departDate(LocalDate.of(2021,05,20))
                .departTime(LocalTime.of(13,50))
                .reachTime(LocalTime.of(14,30))
                .build();
        AirlineGenerateDto airlineGenerateDto2 = AirlineGenerateDto.builder()
                .departure(Departure.BUS)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(13,50))
                .reachTime(LocalTime.of(14,30))
                .build();

        Airline goAir = Airline.createAirline(airlineGenerateDto1);
        Airline backAir = Airline.createAirline(airlineGenerateDto2);
        Airline goAirResult = airlineRepository.save(goAir);
        Airline bakcAirResult = airlineRepository.save(backAir);
        String username = "ppap";
        int adult = 1;
        int child = 1;
        int totalPer = 2;
        int totalPri = 80000;

        ReservationDto reservationDto = new ReservationDto();
        reservationDto.setGoAirId(goAirResult.getId());
        reservationDto.setBackAirId(bakcAirResult.getId());
        reservationDto.setUsername(username);
        reservationDto.setAdultCount(adult);
        reservationDto.setChildCount(child);
        reservationDto.setTotalPerson(adult+child);
        reservationDto.setTotalPrice(totalPri);

        this.mockMvc.perform(post("/api/reservation/new")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(reservationDto)
                ))
                .andDo(print())
                .andExpect(status().isOk())
        .andDo(document("reservation-complete" ,
                links(
                        linkWithRel("index").description("index href"),
                        linkWithRel("self").description("self href"),
                        linkWithRel("my-reservationList").description("내 예약 정보 확인 href"),
                        linkWithRel("reservation-checkIn").description("좌석체크인 href"),
                        linkWithRel("reservation-Info").description("해당 예약 상세 정보 확인 href")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("APPLICATION_JSON_VALUE"),
                        headerWithName(HttpHeaders.ACCEPT).description("HAL_JSON")
                )
                ))
        ;
    }

    @Test
    public void myReservations() throws Exception {
        AirlineGenerateDto airlineGenerateDto1 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.BUS)
                .departDate(LocalDate.of(2021,05,20))
                .departTime(LocalTime.of(13,50))
                .reachTime(LocalTime.of(14,30))
                .build();
        AirlineGenerateDto airlineGenerateDto2 = AirlineGenerateDto.builder()
                .departure(Departure.BUS)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(13,50))
                .reachTime(LocalTime.of(14,30))
                .build();
        Airline goAir = Airline.createAirline(airlineGenerateDto1);
        Airline backAir = Airline.createAirline(airlineGenerateDto2);
        Airline goAirResult = airlineRepository.save(goAir);
        Airline bakcAirResult = airlineRepository.save(backAir);
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
        MemberRole memberRole = MemberRole.ROLE_MEMBER;
        Member member = Member.createMember(createDto,memberRole);
        RefreshTokenProperty r = new RefreshTokenProperty();
        member.issueRefreshToken(r);
        memberRepository.save(member);

        String username = "user1";
        int adult = 1;
        int child = 1;
        int totalPer = 2;
        int totalPri = 80000;

        Reservation reservation
                = Reservation.createReservation(member,goAir,adult,child,adult+child,totalPri);
        Reservation reservation2
                = Reservation.createReservation(member,backAir,adult,child,adult+child,totalPri);

        reservationRepository.save(reservation);
        reservationRepository.save(reservation2);
        int pageNum = 1;
        ForFindPagingDto dto = new ForFindPagingDto(pageNum,username);

        this.mockMvc.perform(get("/api/reservation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
        ;//!!!!!!!!예약 정보들 self-description안해씅ㅁ!!!! 밤에 해랑 21/06/02
    }

    @Test
    public void checkInSeat() throws Exception{
        CheckInRegDto checkInRegDto = new CheckInRegDto();
        checkInRegDto.setReservationId(1L);
        checkInRegDto.setAirlineId(1L);
        checkInRegDto.setTotalPerson(2);
        checkInRegDto.setSelectSeats("A1,A2");

        this.mockMvc.perform(post("/api/reservation/checkIn")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(checkInRegDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("seatList").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
        .andDo(document("seat-checkIn",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json"),
                        headerWithName(HttpHeaders.ACCEPT).description("hal/json")
                ),
                requestFields(
                        fieldWithPath("reservationId").description("해당 예약코드"),
                        fieldWithPath("airlineId").description("해당 예약 항공권 코드"),
                        fieldWithPath("totalPerson").description("해당 예약 총 인원"),
                        fieldWithPath("selectSeats").description("해당 예약 체크인좌석 정보")
                )))
        ;
    }

    @Test
    public void getReservation() throws Exception {

        this.mockMvc.perform(get("/api/reservation/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
        .andDo(document("reservation-info",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api"),
                        linkWithRel("update-reservation").description("예약 수정 href")
                ),
                responseFields(
                        fieldWithPath("reservationId").description("예약 코드 정보"),
                        fieldWithPath("username").description("예약자 id 정보"),
                        fieldWithPath("adult").description("예약된 성인 수 정보"),
                        fieldWithPath("child").description("예약된 아이 수 정보"),
                        fieldWithPath("airlineId").description("예약 항공코드 정보"),
                        fieldWithPath("departure").description("예약 항공권 출발지 정보"),
                        fieldWithPath("distination").description("예약 항공권 도착지 정보"),
                        fieldWithPath("departDate").description("예약 항공권 출발날짜 정보"),
                        fieldWithPath("departTime").description("예약 항공권 출발시간 정보"),
                        fieldWithPath("reachTime").description("예약 항공권 도착시간 정보"),
                        fieldWithPath("totalPrice").description("예약 총 가격 정보"),
                        fieldWithPath("reserveSeats").description("체크인한 좌석 정보"),
                        fieldWithPath("_links.index.href").description("index href"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.profile.href").description("document href for explain this api"),
                        fieldWithPath("_links.update-reservation.href").description("예약수정 링크정보")
                )
                ))
        ;
    }

    @Test
    public void findAllReservation() throws Exception {

        int page = 1;

        ForFindPagingDto forFindPagingDto = new ForFindPagingDto();
        forFindPagingDto.setPageNum(page);

        this.mockMvc.perform(get("/api/reservation/admin")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(forFindPagingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("reservations").exists())
                .andExpect(jsonPath("pageDto").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.reservation-info.href").exists())
        .andDo(document("all-reservation",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api"),
                        linkWithRel("reservation-info").description("해당 예약의 상세정보 href")
                ),
                requestHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json"),
                        headerWithName(HttpHeaders.ACCEPT).description("hal/json")
                )
                ))
                ;

    }

    @Test
    public void cancleReservation() throws Exception {
        Long id = 1L;
        Long airId = 1L;
        int total = 2;
        String username = "user1";
        Member member = memberRepository.findByUsername(username);

        TokenDto accessTokenDto = TokenDto.builder()
                .token(jwtTokenProvider
                        .createToken(member.getUsername(),member.getRole()))
                .build();
        ReservationRemoveDto reservationRemoveDto = ReservationRemoveDto.builder()
                .airlineId(airId)
                .totalPerson(total)
                .build();

        this.mockMvc.perform(delete("/api/reservation/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("Authorization",accessTokenDto.getToken())
                .content(objectMapper.writeValueAsString(reservationRemoveDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("selfLink").exists())
                .andExpect(jsonPath("profileLink").exists())
                .andExpect(jsonPath("indexLink").exists())
        .andDo(document("delete-reservation",
                requestFields(
                        fieldWithPath("airlineId").description("해당 예약의 항공 코드 값"),
                        fieldWithPath("totalPerson").description("해당 예약의 전체 인원 수")
                )
        ))
                ;
    }
}