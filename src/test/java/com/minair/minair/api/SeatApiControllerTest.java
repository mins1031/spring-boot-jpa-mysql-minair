package com.minair.minair.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.dto.seat.CheckInDto;
import com.minair.minair.testconfig.RestDocsConfiguration;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
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
public class SeatApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void checkInTest() throws Exception{
        //Given
        Long airlineId = 5L;
        Long reserve = 101L;
        int total = 2;
        CheckInDto checkInDto = new CheckInDto(airlineId,reserve,total);

        //When
        this.mockMvc.perform(get("/api/checkIn")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(checkInDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("seatList").exists())
                .andExpect(jsonPath("airlineId").exists())
                .andExpect(jsonPath("reservationId").exists())
                .andExpect(jsonPath("totalPerson").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("checkIn",
                links(
                    linkWithRel("self").description("self href"),
                    linkWithRel("profile").description("profile href")
                ),
                requestHeaders(
                    headerWithName(HttpHeaders.ACCEPT).description("헤더 accept"),
                    headerWithName(HttpHeaders.CONTENT_TYPE).description("헤더 content_type")
                ),
                requestFields(
                        fieldWithPath("airlineId").description("항공편 코드"),
                        fieldWithPath("reservationId").description("예약 코드"),
                        fieldWithPath("totalPerson").description("전체 예약자수")
                ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                ),
                responseFields(
                        fieldWithPath("seatList").description("전체 좌석 리스트"),
                        fieldWithPath("airlineId").description("항공편 코드"),
                        fieldWithPath("reservationId").description("예약 코드"),
                        fieldWithPath("totalPerson").description("전체 예약자수"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.profile.href").description("profile href")
                )
                ))
        ;
        //Then
        //우선 리스트 바디가 포스트맨은 나오는데 테스트에는 안나오고 마지막 index 링크도 걸어줄수가 없음...
        //위의 두개가 개선사항이고 다른 리스트 테스트 진행하고 다시 돌아와서 볼것.
    }
}