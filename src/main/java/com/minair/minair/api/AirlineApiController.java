package com.minair.minair.api;

import com.minair.minair.common.ErrorResource;
import com.minair.minair.controller.AdminController;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.AirlineCreateDto;
import com.minair.minair.domain.dto.airline.AirlineDto;
import com.minair.minair.service.AirlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/airline")
public class AirlineApiController {

    private final AirlineService airlineService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "/new",produces = MediaTypes.HAL_JSON_VALUE)
    public ResponseEntity createAirline(@RequestBody @Valid AirlineCreateDto airlineCreateDto,
                                        Errors errors){
        log.info("항공권 등록 post 요청");
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        try {

            Airline airline = Airline.createAirline(airlineCreateDto.getDeparture()
                    , airlineCreateDto.getDistination(), airlineCreateDto.getDepart_date(),
                    airlineCreateDto.getDepart_time(), airlineCreateDto.getReach_time(), 18);

            Link webMvcLinkBuilder = linkTo(methodOn(AirlineApiController.class)
                    .createAirline(airlineCreateDto,errors)).withSelfRel();
            URI createUri = webMvcLinkBuilder.toUri();

            Airline savedAirline = airlineService.createAirline(airline);
            AirlineDto airlineDto = modelMapper.map(savedAirline, AirlineDto.class);
            EntityModel createAirlineResource = EntityModel.of(airlineDto);
            createAirlineResource.add(webMvcLinkBuilder);
            createAirlineResource.add(new Link("/docs/index/html").withRel("profile"));

            return ResponseEntity.created(createUri).body(createAirlineResource);
            //return new ResponseEntity(HttpStatus.CREATED);

            //EntityModel도 결국 자바빈 규칙을 지키는 클래스만을 적용해야 컨버터가 jackson이 잘 json으로 컨버팅해줌
            //즉 올바른 생성자(NO,ALL)두개와 세터,게터를 가진 객체를 of의 파라미터로 사용해줘야함.
        } catch (NullPointerException e){
            e.printStackTrace();
            return new ResponseEntity(e,HttpStatus.UNAUTHORIZED);
        }
    }
}
