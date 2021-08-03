package com.minair.minair.api;

import com.minair.minair.common.BasicResource;
import com.minair.minair.common.ErrorResource;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.common.LinkDto;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.reservation.ReservationRemoveDto;
import com.minair.minair.domain.dto.reservation.*;
import com.minair.minair.domain.dto.seat.SeatDtoForCheckIn;
import com.minair.minair.repository.ReservationRepository;
import com.minair.minair.service.AirlineService;
import com.minair.minair.service.ReservationService;
import com.minair.minair.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservation", produces = MediaTypes.HAL_JSON_VALUE)
public class ReservationApiController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final AirlineService airlineService;
    private final SeatService seatService;

    //예약 완료 페이지
    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity complete(@RequestBody @Valid ReservationDto reservationDto,
                                   Errors errors){
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        List<ReservationResultDto> resultDtos = reservationService.reservation(reservationDto);
        if (resultDtos.isEmpty())
            return ResponseEntity.noContent().build();

        ReservationCompleteResultDto resultDto = ReservationCompleteResultDto.builder()
                .goAirline(resultDtos.get(0))
                .backAirline(resultDtos.get(1))
                .build();

        BasicResource reservationResource = new BasicResource(resultDto);
        reservationResource.add(new Link("/api/reservation/new").withSelfRel());
        reservationResource.add(new Link("/api/reservation").withRel("my-reservationList"));
        reservationResource.add(new Link("/api/checkIn").withRel("reservation-checkIn"));
        reservationResource.add(new Link("/api/reservation").withRel("reservation-Info"));

        return ResponseEntity.ok().body(reservationResource);
    }

    //내 예약 목록 조회 api
    //RequestBody 바꿔야됨
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity myReservations(@ModelAttribute @Valid ForFindPagingDto forFindPagingDto,
                                         Errors errors){

        if (errors.hasErrors() || forFindPagingDto.getUsername() == null)
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        ReservationsResultDto reservationsResult =
                reservationService.findReservations(forFindPagingDto.getUsername(), forFindPagingDto.getPageNum());

        BasicResource reservationResource = new BasicResource(reservationsResult);
        reservationResource.add(new Link("/api/checkIn").withRel("reservation-checkIn"));
        reservationResource.add(new Link("/api/reservation").withRel("reservation-Info"));

        return ResponseEntity.ok(reservationResource);
    }

    //좌석 체크인 등록 api
    @PostMapping("/checkIn")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity checkInSeat(@RequestBody @Valid CheckInRegDto checkInRegDto,
                                      Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        reservationService.checkSeat(checkInRegDto.getReservationId(), checkInRegDto.getSelectSeats());
        airlineService.subSeatCount(checkInRegDto.getAirlineId(), checkInRegDto.getTotalPerson());
        List<SeatDtoForCheckIn> seatDtoList
                = seatService.checkInSeats(checkInRegDto.getAirlineId(), checkInRegDto.getSelectSeats());

        CheckinResultDto checkinResultDto = new CheckinResultDto(seatDtoList);

        BasicResource resultResource = new BasicResource(checkinResultDto);
        resultResource.add(new Link("/api/reservation/checkIn").withSelfRel());

        return ResponseEntity.ok().body(resultResource);
    }

    //단건예약 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity getReservation(@PathVariable("id")  Long reservationId){

        ReservationDetailInfoDto reservationDetailInfoDto = reservationService.findOneReservation(reservationId);
        if (reservationDetailInfoDto == null)
            return ResponseEntity.noContent().build();

        BasicResource reservationResource = new BasicResource(reservationDetailInfoDto);
        reservationResource.add(linkTo(ReservationApiController.class).slash(reservationId).withSelfRel());
        reservationResource.add(linkTo(ReservationApiController.class).slash(reservationId).withRel("update-reservation"));

        return ResponseEntity.ok().body(reservationResource);
    }

    //RequestBody 바꿔야됨
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity findAllReservation(@RequestBody @Valid ForFindPagingDto forFindPagingDto,
                                             Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        ReservationsResultDto allReservation = reservationService.findAll(forFindPagingDto.getPageNum());
        if (allReservation == null)
            return ResponseEntity.noContent().build();

        BasicResource reservationResource = new BasicResource(allReservation);
        reservationResource.add(linkTo(ReservationApiController.class).withSelfRel());
        reservationResource.add(new Link("/api/reservation/{id}").withRel("reservation-info"));

        return ResponseEntity.ok().body(reservationResource);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity cancleReservation(@PathVariable Long id,
                                            @RequestBody @Valid ReservationRemoveDto reservationRemoveDto,
                                            Errors errors){
        if (errors.hasErrors() || id == null)
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        Optional<Reservation> optionalReservation = reservationRepository.findById(id);
        Reservation findReservation = optionalReservation.get();
        if (findReservation == null)
            return ResponseEntity.noContent().build();

        if (findReservation.getReserveSeat() != null){
            airlineService.subSeatCount(reservationRemoveDto.getAirlineId(), reservationRemoveDto.getTotalPerson());
            seatService.cancleSeats(reservationRemoveDto.getAirlineId(), findReservation.getReserveSeat());
            reservationService.remove(findReservation);
        } else {
            reservationService.remove(findReservation);
        }

        BasicResource deleteResource = new BasicResource();

        return ResponseEntity.ok().body(deleteResource);
    }

    /**
     * 예약 api완료, 리펙토링 요소는 일단 null값이 있는 경우의 예외처리. 예외를 reservationNullException
     * 이런 식으로 만들어줄것.+ 권한처리 필요한 메서드 @PreAuthorize("hasRole('ROLE_ADMIN')")해줄것
     * */
}
