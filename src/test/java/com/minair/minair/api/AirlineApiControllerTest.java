package com.minair.minair.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.dto.airline.AirlineSearchDto;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.airline.AirlineCreateDto;
import com.minair.minair.domain.dto.member.MemberCreateDto;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.testconfig.RestDocsConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class AirlineApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AirlineRepository airlineRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    public void testAirlineCreate() throws Exception {
        AirlineCreateDto airlineCreateDto = AirlineCreateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.DAE)
                .depart_date(LocalDate.of(2021,06,11))
                .depart_time(LocalTime.of(13,20))
                .reach_time(LocalTime.of(13,55))
                .build();

        System.out.println(airlineCreateDto);

        this.mockMvc.perform(post("/api/airline/new")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                //.contentType(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(airlineCreateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("departure").exists())
                .andExpect(jsonPath("distination").exists())
                .andExpect(jsonPath("depart_date").exists())
                .andExpect(jsonPath("depart_time").exists())
                .andExpect(jsonPath("reach_time").exists())
                .andExpect(jsonPath("aboveseat").exists())
                .andExpect(jsonPath("_links.index").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("airline-create" ,
                links(
                        linkWithRel("index").description("index page"),
                        linkWithRel("self").description("self href"),
                        linkWithRel("profile").description("Document for explain this api")
                ),
                requestFields(
                        fieldWithPath("departure").description("departure of created airline"),
                        fieldWithPath("distination").description("distination of created airline"),
                        fieldWithPath("depart_date").description("depart_date of created airline"),
                        fieldWithPath("depart_time").description("depart_time of created airline"),
                        fieldWithPath("reach_time").description("reach_time of created airline")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                responseFields(
                        fieldWithPath("departure").description("departure of created airline"),
                        fieldWithPath("distination").description("distination of created airline"),
                        fieldWithPath("depart_date").description("depart_date of created airline"),
                        fieldWithPath("depart_time").description("depart_time of created airline"),
                        fieldWithPath("reach_time").description("reach_time of created airline"),
                        fieldWithPath("aboveseat").description("aboveseat(count of rest seats) of created airline"),
                        fieldWithPath("_links.index.href").description("index page href"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.profile.href").description("Document link for explain this api")
                )
        ))

        ;
    }

    @Before
    public void be(){
        AirlineGenerateDto airlineGenerateDto1 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.DAE)
                .departDate(LocalDate.of(2021,05,20))
                .departTime(LocalTime.of(13,55))
                .reachTime(LocalTime.of(14,20))
                .build();
        Airline airline1 = Airline.createAirline(airlineGenerateDto1);
        AirlineGenerateDto airlineGenerateDto2 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.BUS)
                .departDate(LocalDate.of(2021,05,20))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline2 = Airline.createAirline(airlineGenerateDto2);
        AirlineGenerateDto airlineGenerateDto3 = AirlineGenerateDto.builder()
                .departure(Departure.BUS)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(13,55))
                .reachTime(LocalTime.of(14,20))
                .build();
        Airline airline3 = Airline.createAirline(airlineGenerateDto3);
        AirlineGenerateDto airlineGenerateDto4 = AirlineGenerateDto.builder()
                .departure(Departure.BUS)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline4 = Airline.createAirline(airlineGenerateDto4);
        AirlineGenerateDto airlineGenerateDto5 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.ICN)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline5 = Airline.createAirline(airlineGenerateDto5);
        AirlineGenerateDto airlineGenerateDto6 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.ICN)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline6 = Airline.createAirline(airlineGenerateDto6);
        AirlineGenerateDto airlineGenerateDto7 = AirlineGenerateDto.builder()
                .departure(Departure.ICN)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline7 = Airline.createAirline(airlineGenerateDto7);
        AirlineGenerateDto airlineGenerateDto8 = AirlineGenerateDto.builder()
                .departure(Departure.ICN)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline8 = Airline.createAirline(airlineGenerateDto8);
        AirlineGenerateDto airlineGenerateDto9 = AirlineGenerateDto.builder()
                .departure(Departure.GWANG)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline9 = Airline.createAirline(airlineGenerateDto9);
        AirlineGenerateDto airlineGenerateDto10 = AirlineGenerateDto.builder()
                .departure(Departure.GWANG)
                .distination(Distination.JEJU)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline10 = Airline.createAirline(airlineGenerateDto10);
        AirlineGenerateDto airlineGenerateDto11 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.GWANG)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline11 = Airline.createAirline(airlineGenerateDto11);
        AirlineGenerateDto airlineGenerateDto12 = AirlineGenerateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.GWANG)
                .departDate(LocalDate.of(2021,05,23))
                .departTime(LocalTime.of(14,55))
                .reachTime(LocalTime.of(15,20))
                .build();
        Airline airline12 = Airline.createAirline(airlineGenerateDto12);

        airlineRepository.save(airline1);
        airlineRepository.save(airline2);
        airlineRepository.save(airline3);
        airlineRepository.save(airline4);
        airlineRepository.save(airline5);
        airlineRepository.save(airline6);
        airlineRepository.save(airline7);
        airlineRepository.save(airline8);
        airlineRepository.save(airline9);
        airlineRepository.save(airline10);
        airlineRepository.save(airline11);
        airlineRepository.save(airline12);

    }

    @Test
    public void searchAirline() throws Exception{

        Departure departure = Departure.JEJU;
        Distination distination = Distination.BUS;
        LocalDate depart_date = LocalDate.of(2021,05,20);
        LocalDate comebackDate = LocalDate.of(2021,05,23);
        int adult = 2;
        int child = 1;
        AirlineSearchDto airlineSearchDto
                = new AirlineSearchDto(departure,distination,depart_date,comebackDate,adult,child);

        this.mockMvc.perform(get("/api/airline/search")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .param("departure", "JEJU")
                .param("distination", "BUS")
                .param("depart_date", "2021-05-20")
                .param("comebackDate", "2021-05-23")
                .param("adult", "2")
                .param("child", "1")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("goAirlineList").isNotEmpty())
                .andExpect(jsonPath("backAirlineList").isNotEmpty())
                .andExpect(jsonPath("adult").exists())
                .andExpect(jsonPath("child").exists())
                .andExpect(jsonPath("_links.index").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.reserve-complete").exists())
               .andDo(document("airline-search",
                        links(
                                linkWithRel("index").description("index page href"),
                                linkWithRel("self").description("self href"),
                                linkWithRel("profile").description("document href for explain this api"),
                                linkWithRel("reserve-complete").description("reservation complete href")
                        ),
                        requestParameters(
                                parameterWithName("departure").description("출발지 정보 파라미터"),
                                parameterWithName("distination").description("도착지 정보 파라미터"),
                                parameterWithName("depart_date").description("출발날 정보 파라미터"),
                                parameterWithName("comebackDate").description("돌아오는날 정보 파라미터"),
                                parameterWithName("adult").description("성인인원수 정보 파라미터"),
                                parameterWithName("child").description("아이인원수 정보 파라미터")
                        )
                ))
                ;

    }

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    @Rollback(value = false)
    public void allList() throws Exception{

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
        Member member1 = memberRepository.findByUsername(username);
        TokenDto tokenDto = new TokenDto(jwtTokenProvider.createToken(
                member1.getUsername(),member1.getRole()));

        this.mockMvc.perform(get("/api/airline")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .header("Authorization",tokenDto.getToken())
                .param("pageNum","2"))
                //.content(objectMapper.writeValueAsString(forFindPagingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("airlineList").exists())
                .andExpect(jsonPath("pageDto").exists())
                .andExpect(jsonPath("_links.index").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("all-airline",
                        links(
                                linkWithRel("index").description("index page href"),
                                linkWithRel("self").description("self href"),
                                linkWithRel("profile").description("document href for explain this api")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json"),
                                headerWithName(HttpHeaders.ACCEPT).description("hal")
                        ),
                        requestParameters(
                                parameterWithName("pageNum").description("현재 페이지 정보")
                        )
                ))
        ;
        //api는 요청도 웬만하면 @RequestParam보단 dto하나 만들어서 @RequestBody로 받아줄것.
        //테스트 구동할땐 꼭 디비 분리된거 잊지말고 값 넣어주고 테스트 할것.
        //mock에 단순 wrapper클래스은 int를 넣었더니 캐스팅 오류가 발생함. 앵간하면 1번주석의
        //내용과 같이 dto로 요청을 받도록 하여 에러를 줄일것.
    }
}