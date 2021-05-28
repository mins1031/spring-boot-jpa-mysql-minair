package com.minair.minair.api;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.AirlineSearchDto;
import com.minair.minair.domain.dto.ForFindPagingDto;
import com.minair.minair.domain.dto.airline.AirlineCreateDto;
import com.minair.minair.domain.dto.airline.AirlineSearchApiDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.service.AirlineService;
import com.minair.minair.testconfig.RestDocsConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
@Transactional

public class AirlineApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AirlineRepository airlineRepository;

    @Autowired
    EntityManager em;

    @Test
    public void testAirlineCreate() throws Exception {
        AirlineCreateDto airlineCreateDto = AirlineCreateDto.builder()
                .departure(Departure.JEJU)
                .distination(Distination.DAE)
                .depart_date(LocalDate.of(2021,06,11))
                .depart_time(LocalTime.of(13,20))
                .reach_time(LocalTime.of(13,55))
                .build();


        this.mockMvc.perform(post("/api/airline/new")
                .contentType(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(airlineCreateDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("departure").exists())
                .andExpect(jsonPath("distination").exists())
                .andExpect(jsonPath("depart_date").exists())
                .andExpect(jsonPath("depart_time").exists())
                .andExpect(jsonPath("reach_time").exists())
                .andExpect(jsonPath("aboveseat").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("airline-create" ,
                links(
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
                        fieldWithPath("id").description("id of created airline"),
                        fieldWithPath("departure").description("departure of created airline"),
                        fieldWithPath("distination").description("distination of created airline"),
                        fieldWithPath("depart_date").description("depart_date of created airline"),
                        fieldWithPath("depart_time").description("depart_time of created airline"),
                        fieldWithPath("reach_time").description("reach_time of created airline"),
                        fieldWithPath("aboveseat").description("aboveseat(count of rest seats) of created airline"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.profile.href").description("Document link for explain this api")
                )
        ))

        ;
    }


    public void be(){
        Airline airline1 = Airline.createAirline(Departure.JEJU,Distination.BUS,
                LocalDate.of(2021,05,20), LocalTime.of(13,55),
                LocalTime.of(14,20),18);
        Airline airline2 = Airline.createAirline(Departure.JEJU,Distination.BUS,
                LocalDate.of(2021,05,20), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline3 = Airline.createAirline(Departure.BUS,Distination.JEJU,
                LocalDate.of(2021,05,23), LocalTime.of(13,55),
                LocalTime.of(14,20),18);
        Airline airline4 = Airline.createAirline(Departure.BUS,Distination.JEJU,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline5 = Airline.createAirline(Departure.JEJU,Distination.ICN,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline6 = Airline.createAirline(Departure.JEJU,Distination.ICN,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline7 = Airline.createAirline(Departure.ICN,Distination.JEJU,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline8 = Airline.createAirline(Departure.ICN,Distination.JEJU,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline9 = Airline.createAirline(Departure.GWANG,Distination.JEJU,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline10 = Airline.createAirline(Departure.GWANG,Distination.JEJU,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline11 = Airline.createAirline(Departure.JEJU,Distination.GWANG,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);
        Airline airline12 = Airline.createAirline(Departure.JEJU,Distination.GWANG,
                LocalDate.of(2021,05,23), LocalTime.of(14,55),
                LocalTime.of(15,20),18);

        em.persist(airline1);
        em.persist(airline2);
        em.persist(airline3);
        em.persist(airline4);
        em.persist(airline5);
        em.persist(airline6);
        em.persist(airline7);
        em.persist(airline8);
        em.persist(airline9);
        em.persist(airline10);
        em.persist(airline11);
        em.persist(airline12);

        em.flush();
        em.clear();
    }

    @Test
    @Rollback(value = false)
    public void searchAirline() throws Exception{

        be();
        Departure departure = Departure.JEJU;
        Distination distination = Distination.BUS;
        LocalDate depart_date = LocalDate.of(2021,05,20);
        LocalDate comebackDate = LocalDate.of(2021,05,23);
        int adult = 2;
        int child = 1;
        AirlineSearchDto airlineSearchDto
                = new AirlineSearchDto(departure,distination,depart_date,comebackDate,adult,child);

        this.mockMvc.perform(get("/api/airline/search")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(airlineSearchDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("goAirlineList").isNotEmpty())
                .andExpect(jsonPath("backAirlineList").isNotEmpty())
                .andExpect(jsonPath("adult").exists())
                .andExpect(jsonPath("child").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.reserve-complete").exists())
        .andDo(document("airline-search",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("profile").description("document href for explain this api"),
                        linkWithRel("reserve-complete").description("reservation complete href")
                ),
                requestFields(
                        fieldWithPath("departure").description("출발지 정보 파라미터"),
                        fieldWithPath("distination").description("도착지 정보 파라미터"),
                        fieldWithPath("depart_date").description("출발날 정보 파라미터"),
                        fieldWithPath("comebackDate").description("돌아오는날 정보 파라미터"),
                        fieldWithPath("adult").description("성인인원수 정보 파라미터"),
                        fieldWithPath("child").description("아이인원수 정보 파라미터"),
                        fieldWithPath("totalPerson").description("총 인원수 정보")
                )
                ))
        ;
    }

    @Test
    @Rollback(value = false)
    public void allList() throws Exception{
        be();

        int pageNum = 2;
        ForFindPagingDto forFindPagingDto = new ForFindPagingDto();
        forFindPagingDto.setPageNum(pageNum);

        this.mockMvc.perform(get("/api/airline")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(forFindPagingDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("airlineList").exists())
                .andExpect(jsonPath("totalPage").exists())
                .andExpect(jsonPath("totalElements").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("all-airline",
                        links(
                                linkWithRel("self").description("self href"),
                                linkWithRel("profile").description("document href for explain this api")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("application/json"),
                                headerWithName(HttpHeaders.ACCEPT).description("hal")
                        ),
                        requestFields(
                                fieldWithPath("pageNum").description("현재 페이지 정보")
                        )
                ))
        ;
    }
}