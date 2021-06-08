package com.minair.minair.api;

import com.minair.minair.common.ErrorResource;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.airline.AirlineSearchDto;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.airline.AirlineCreateDto;
import com.minair.minair.domain.dto.airline.AirlineDto;
import com.minair.minair.domain.dto.airline.AirlineSearchApiDto;
import com.minair.minair.domain.dto.airline.QueryAirlinesDto;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.minair.minair.service.AirlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/airline",produces = MediaTypes.HAL_JSON_VALUE)
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
            createAirlineResource.add(new Link("/docs/index.html").withRel("profile"));

            return ResponseEntity.created(createUri).body(createAirlineResource);
            //return new ResponseEntity(HttpStatus.CREATED);

            //EntityModel도 결국 자바빈 규칙을 지키는 클래스만을 적용해야 컨버터가 jackson이 잘 json으로 컨버팅해줌
            //즉 올바른 생성자(NO,ALL)두개와 세터,게터를 가진 객체를 of의 파라미터로 사용해줘야함.
        } catch (NullPointerException e){
            e.printStackTrace();
            return new ResponseEntity(e,HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping(value = "/search",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity searchAirline(@RequestBody @Valid AirlineSearchDto airlineSearchDto,
                                        Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        List<Airline> airlineList = airlineService.searchAirlines(airlineSearchDto);

        /*if (airlineList.isEmpty())
            return new ResponseEntity(new NotFoundAirlines(),HttpStatus.INTERNAL_SERVER_ERROR);
*/
        List<AirlineDto> goAirlineList = new ArrayList<>();
        for (Airline a: airlineList) {
            goAirlineList.add(modelMapper.map(a,AirlineDto.class));
        }

        String convertDeparture = airlineSearchDto.getDeparture().toString();
        String convertDistination = airlineSearchDto.getDistination().toString();

        Departure departure = Departure.valueOf(convertDistination);
        Distination distination = Distination.valueOf(convertDeparture);

        AirlineSearchDto backAirlineDto = new AirlineSearchDto(departure,distination,
                airlineSearchDto.getComebackDate(),airlineSearchDto.getAdult(),airlineSearchDto.getChild());
        List<Airline> backList = airlineService.searchAirlines(backAirlineDto);
        List<AirlineDto> backAirlineList = new ArrayList<>();
        for (Airline a: backList) {
            backAirlineList.add(modelMapper.map(a,AirlineDto.class));
        }
        AirlineSearchApiDto<List<AirlineDto>> airlineSearchApiDto
                = new AirlineSearchApiDto<>(goAirlineList,backAirlineList,airlineSearchDto.getAdult(),
                airlineSearchDto.getChild());
        EntityModel airlineResource = EntityModel.of(airlineSearchApiDto);
        airlineResource.add(new Link("/api/airline/search").withSelfRel());
        airlineResource.add(new Link("/reservation/complete").withRel("reserve-complete"));
        airlineResource.add(new Link("/docs/index.html").withRel("profile"));
        return new ResponseEntity(airlineResource,HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity queryAirlines(@RequestBody @Valid ForFindPagingDto forFindPagingDto,
                                        Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().build();

        Page<Airline> airlinePage = airlineService.findAllAirline(forFindPagingDto.getPageNum());
        List<AirlineDto> airlineDtoList = new ArrayList<>();

        for (Airline a:airlinePage.getContent()) {
            airlineDtoList.add(modelMapper.map(a,AirlineDto.class));
        }
        PageDto pageDto = new PageDto(forFindPagingDto.getPageNum(),10,
                airlinePage.getTotalElements(),airlinePage.getTotalPages());
        QueryAirlinesDto queryAirlinesDto = QueryAirlinesDto.builder()
                .airlineList(airlineDtoList)
                .pageDto(pageDto)
                .build();

        EntityModel pageResource = EntityModel.of(queryAirlinesDto);
        pageResource.add(linkTo(AirlineApiController.class).withSelfRel());
        pageResource.add(new Link("/docs/index.htm").withRel("profile"));
        return ResponseEntity.ok(pageResource);
    }

    //더 추가할거 없는가..? 항공권 검색, 등록, 모든 항공권 조회,  일반 웹은 구현 안했지만 항공권 단건조회와 수정...
    //도 필요할듯.우선 구현해놨던거 구현하고 추가할것.
}
