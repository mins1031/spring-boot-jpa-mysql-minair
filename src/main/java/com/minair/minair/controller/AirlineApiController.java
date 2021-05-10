package com.minair.minair.controller;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.AirlineCreateDto;
import com.minair.minair.service.AirlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AirlineApiController {

    private final AirlineService airlineService;
    //@ModelAttribute("airlineCreate")
    @PostMapping("/airline/new")
    public ResponseEntity createAirline(@RequestBody AirlineCreateDto airlineCreateDto){
        log.info("항공권 등록 post 요청");
        System.out.println(airlineCreateDto.getDeparture());
        System.out.println(airlineCreateDto.getDistination());
        System.out.println(airlineCreateDto.getDepart_date());
        System.out.println(airlineCreateDto.getDepart_time());
        System.out.println(airlineCreateDto.getReach_time());

        try {
            Airline airline = Airline.createAirline(airlineCreateDto.getDeparture()
                    , airlineCreateDto.getDistination(), airlineCreateDto.getDepart_date(),
                    airlineCreateDto.getDepart_time(), airlineCreateDto.getReach_time(), 18);
            airlineService.createAirline(airline);
            return new ResponseEntity(HttpStatus.OK);
        } catch (NullPointerException e){
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}
