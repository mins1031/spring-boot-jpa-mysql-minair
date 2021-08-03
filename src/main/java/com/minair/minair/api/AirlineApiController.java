package com.minair.minair.api;

import com.minair.minair.common.BasicResource;
import com.minair.minair.common.ErrorResource;
import com.minair.minair.common.ServerConstValue;
import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.*;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
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
@RequestMapping(value = "/api/airline")
public class AirlineApiController {

    private final AirlineService airlineService;
    private final ModelMapper modelMapper;

    @PostMapping(value = "/new")
    public ResponseEntity createAirline(@RequestBody @Valid AirlineCreateDto airlineCreateDto,
                                        Errors errors){
        log.info("항공권 등록 post 요청");
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        try {
            AirlineDto savedAirline = airlineService.createAirline(airlineCreateDto);

            Link selfRel = linkTo(methodOn(AirlineApiController.class)
                    .createAirline(airlineCreateDto,errors)).withSelfRel();

            BasicResource createAirlineResource = new BasicResource(savedAirline);
            createAirlineResource.add(selfRel);

            URI createUri = selfRel.toUri();
            return ResponseEntity.created(createUri).body(createAirlineResource);
        } catch (RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //RequestBody 바꿔야됨
    @GetMapping(value = "/search")
    public ResponseEntity searchAirline(@ModelAttribute @Valid AirlineSearchDto airlineSearchDto,
                                        Errors errors) {
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        AirlineSearchApiDto<List<AirlineDto>> airlineSearchApiDto
                = airlineService.searchAirlines(airlineSearchDto);
        //가는편,오는편,인원수 정보 서비스에서 리턴

        BasicResource airlineResource = new BasicResource(airlineSearchApiDto);
        airlineResource.add(new Link("/api/airline/search").withSelfRel());
        airlineResource.add(new Link("/reservation/complete").withRel("reserve-complete"));

        return new ResponseEntity(airlineResource,HttpStatus.OK);
    }

    //RequestBody 바꿔야됨
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity queryAirlines(@ModelAttribute @Valid ForFindPagingDto forFindPagingDto,
                                        Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().build();

        QueryAirlinesDto queryAirlinesDto = airlineService.findAllAirline(forFindPagingDto.getPageNum());

        BasicResource airlineResource = new BasicResource(queryAirlinesDto);
        airlineResource.add(linkTo(AirlineApiController.class).withSelfRel());

        return ResponseEntity.ok(airlineResource);
    }

    //더 추가할거 없는가..? 항공권 검색, 등록, 모든 항공권 조회,  일반 웹은 구현 안했지만 항공권 단건조회와 수정...도 필요할듯.우선 구현해놨던거 구현하고 추가할것.
}
