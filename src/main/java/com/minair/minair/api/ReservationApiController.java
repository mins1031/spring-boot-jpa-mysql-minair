package com.minair.minair.api;

import com.minair.minair.common.ErrorResource;
import com.minair.minair.controller.HomeController;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.ForFindPagingDto;
import com.minair.minair.domain.dto.PageDto;
import com.minair.minair.domain.dto.airline.QueryAirlinesDto;
import com.minair.minair.domain.dto.reservation.*;
import com.minair.minair.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservation", produces = MediaTypes.HAL_JSON_VALUE)
public class ReservationApiController {

    private final ReservationService reservationService;
    private final ModelMapper modelMapper;

    @PostMapping("/new")
    public ResponseEntity complete(@RequestBody @Valid ReservationDto reservationDto,
                                   Errors errors){
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        List<Reservation> reservationList = reservationService.reservation(reservationDto);
        if (reservationList.isEmpty())
            return ResponseEntity.noContent().build();

        List<ReservationResultDto> resultDtos = new ArrayList<>();
        for (Reservation r: reservationList) {
            resultDtos.add(modelMapper.map(r,ReservationResultDto.class));
        }

        ReservationCompleteResultDto rcrDtos = ReservationCompleteResultDto.builder()
                .goAirline(resultDtos.get(0))
                .backAirline(resultDtos.get(1))
                .build();

        EntityModel reservationResource = EntityModel.of(rcrDtos);
        reservationResource.add(new Link("/index").withRel("index"));
        reservationResource.add(new Link("/api/reservation/new").withSelfRel());
        reservationResource.add(new Link("/api/reservation").withRel("my-reservationList"));
        reservationResource.add(new Link("/api/checkIn").withRel("reservation-checkIn"));
        reservationResource.add(new Link("/api/reservation").withRel("reservation-Info"));

        return ResponseEntity.ok().body(reservationResource);
    }

    @GetMapping
    public ResponseEntity myReservations(@RequestBody @Valid ForFindPagingDto forFindPagingDto,
                                         Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        if (forFindPagingDto.getUsername() == null)
            return ResponseEntity.badRequest().build();

        Page<Reservation> reservationPage =
                reservationService.findReservation(forFindPagingDto.getUsername(), forFindPagingDto.getPageNum());
        if (reservationPage.getContent().isEmpty()) {
            log.info("noContent");
            return ResponseEntity.noContent().build();
        }
        List<ReservationResultDto> resultDtos = new ArrayList<>();
        for (Reservation r: reservationPage.getContent()) {
            resultDtos.add(modelMapper.map(r,ReservationResultDto.class));
        }
        List<Reservation> teamList = reservationPage.getContent();
        List<ReservationResultDto> realList = teamList.stream()
                .map(r -> new ReservationResultDto(r.getId(),r.getMember(),r.getAirline(),r.getAdultCount(),
                        r.getChildCount(),r.getTotalPerson(),r.getTotalPrice(),r.getReserveSeat()))
                .collect(Collectors.toList());

        PageDto pageDto  = new PageDto(forFindPagingDto.getPageNum(), 10 ,
                reservationPage.getTotalElements(),
                reservationPage.getTotalPages());

        ReservationsResultDto result = ReservationsResultDto.builder()
                .reservations(resultDtos)
                .pageDto(pageDto)
                .build();

        EntityModel reservationResource = EntityModel.of(result);
        reservationResource.add(linkTo(ReservationApiController.class).withSelfRel());
        reservationResource.add(new Link("/").withRel("index"));
        reservationResource.add(new Link("/docs/index.html").withRel("profile"));
        reservationResource.add(new Link("/api/checkIn").withRel("reservation-checkIn"));
        reservationResource.add(new Link("/api/reservation").withRel("reservation-Info"));

        return ResponseEntity.ok(result);
    }
}
