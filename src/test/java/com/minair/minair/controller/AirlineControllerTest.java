package com.minair.minair.controller;

import com.minair.minair.domain.Airline;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AirlineControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void testSearch() throws Exception{
        MvcResult airlines =  mockMvc.perform(MockMvcRequestBuilders
                .get("/airline?departure=ICN&distination=JEJU&depart_date=2021-04-03&people=3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        System.out.println(airlines);
        //4/10 6:45 일단 h2디비의 상황에선 200이 뜨는데 마리아db설정하고 다시 해봐야겠다..
    }

}