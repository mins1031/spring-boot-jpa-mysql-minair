package com.minair.minair.api;

import com.minair.minair.common.ErrorResource;
import com.minair.minair.controller.HomeController;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.reservation.ReservationCompleteResultDto;
import com.minair.minair.domain.dto.reservation.ReservationDto;
import com.minair.minair.domain.dto.reservation.ReservationResultDto;
import com.minair.minair.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

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
        reservationResource.add(new Link("/api/reservation/reseravtions").withRel("my-reservationList"));
        reservationResource.add(new Link("/api/checkIn").withRel("reservation-checkIn"));
        reservationResource.add(new Link("/api/reservation/info").withRel("reservation-Info"));

        return ResponseEntity.ok().body(reservationResource);
    }
}
