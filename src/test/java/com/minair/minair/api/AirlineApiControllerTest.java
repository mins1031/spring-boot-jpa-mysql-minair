package com.minair.minair.api;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.dto.airline.AirlineCreateDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.service.AirlineService;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
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
}