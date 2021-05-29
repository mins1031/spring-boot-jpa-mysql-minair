package com.minair.minair.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.reservation.ReservationDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.testconfig.RestDocsConfiguration;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @Test
    public void newTest() throws Exception {
        Airline goAir = Airline.createAirline(Departure.JEJU, Distination.BUS, LocalDate.of(2021,05,20),
                LocalTime.of(13,50),LocalTime.of(14,30),18);
        Airline backAir = Airline.createAirline(Departure.BUS, Distination.DAE, LocalDate.of(2021,05,23),
                LocalTime.of(13,50),LocalTime.of(14,30),18);
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
}