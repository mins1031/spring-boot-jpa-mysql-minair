package com.minair.minair.api;

import com.minair.minair.common.ErrorResource;
import com.minair.minair.controller.HomeController;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.seat.CheckInDto;
import com.minair.minair.domain.dto.seat.SeatDtoForCheckIn;
import com.minair.minair.domain.dto.seat.SeatListResultDto;
import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SeatApiController {

    private final SeatService seatService;
    private final ModelMapper modelMapper;

    @GetMapping(value = "/api/checkIn", produces = MediaTypes.HAL_JSON_VALUE)
    //@PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity checkInSeat(@RequestBody @Valid CheckInDto checkInDto,
                                      Errors errors){
        if (errors.hasErrors()) {
            ErrorResource errorResource = new ErrorResource(errors);
            //errorResource.add(linkTo(methodOn(ReservationRestController.class).reservations).withRel("back"));
            //위의 이전링크는 ReservationRestController의 reservations메서드 추가한후 여기도 주석 풀어줄것.
            return ResponseEntity.badRequest().body(errorResource);
        }

        List<Seat> seats = seatService.checkInList(checkInDto.getAirlineId());
        if (seats == null) {
            log.info("null!");
            return new ResponseEntity(new NotFoundAirlines(), HttpStatus.NOT_FOUND);
        }
        List<SeatDtoForCheckIn> convertSeats = new ArrayList<>();
        for (Seat s :seats) {
            convertSeats.add(modelMapper.map(s,SeatDtoForCheckIn.class));
            System.out.println(convertSeats.size());
        }
        SeatListResultDto<SeatDtoForCheckIn> result = new SeatListResultDto(convertSeats,
                checkInDto.getAirlineId(), checkInDto.getReservationId(), checkInDto.getTotalPerson());
        EntityModel seatResult = EntityModel.of(result);
        seatResult.add(linkTo(methodOn(SeatApiController.class)
                .checkInSeat(checkInDto,errors))
                .withSelfRel());
        //seatResult.add(linkTo(methodOn(HomeController.class).index()).withRel("index"));

        return ResponseEntity.ok().body(seatResult);
    }
}
