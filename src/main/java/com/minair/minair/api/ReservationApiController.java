package com.minair.minair.api;

import com.minair.minair.common.ErrorResource;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.common.LinkDto;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.reservation.ReservationRemoveDto;
import com.minair.minair.domain.dto.reservation.*;
import com.minair.minair.domain.dto.seat.SeatDtoForCheckIn;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/reservation", produces = MediaTypes.HAL_JSON_VALUE)
public class ReservationApiController {

    private final ReservationService reservationService;
    private final AirlineService airlineService;
    private final SeatService seatService;
    private final ModelMapper modelMapper;

    //예약 완료 페이지
    @PostMapping("/new")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
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

    //내 예약 목록 조회 api
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MEMBER')")
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
        List<Reservation> teamList = reservationPage.getContent();
        List<ReservationResultApiDto> resultDtos = new ArrayList<>();
        for (Reservation r: teamList) {
            resultDtos.add(modelMapper.map(r,ReservationResultApiDto.class));
        }

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

        return ResponseEntity.ok(reservationResource);
    }

    //좌석 체크인 등록 api
    @PostMapping("/checkIn")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity checkInSeat(@RequestBody @Valid CheckInRegDto checkInRegDto,
                                      Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        List<Seat> seats = new ArrayList<>();

        try {
            reservationService.checkSeat(checkInRegDto.getReservationId(), checkInRegDto.getSelectSeats());
            airlineService.subSeatCount(checkInRegDto.getAirlineId(), checkInRegDto.getTotalPerson());
            seats = seatService.checkInSeats(checkInRegDto.getAirlineId(), checkInRegDto.getSelectSeats());
        } catch (Exception e){
            e.printStackTrace();
            return new ResponseEntity(e,HttpStatus.INTERNAL_SERVER_ERROR);
        }

        List<SeatDtoForCheckIn> resultList = new ArrayList<>();
        for (Seat s: seats) {
            resultList.add(modelMapper.map(s,SeatDtoForCheckIn.class));
        }

        CheckinResultDto checkinResultDto = new CheckinResultDto(resultList);

        EntityModel resultResource = EntityModel.of(checkinResultDto);
        resultResource.add(new Link("/").withRel("index"));
        resultResource.add(new Link("/api/reservation/checkIn").withSelfRel());
        resultResource.add(new Link("/docs/index").withRel("profile"));

        return ResponseEntity.ok().body(resultResource);
    }

    //단건예약 상세 조회
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity getReservation(@PathVariable("id")  Long reservationId){

        Reservation reservation = reservationService.findOneReservation(reservationId);
        if (reservation == null)
            return ResponseEntity.noContent().build();

        ReservationDetailInfoDto infoDto
                = ReservationDetailInfoDto.ReservationDetailInfoDto(reservation);

        EntityModel resultResource = EntityModel.of(infoDto);
        resultResource.add(new Link("/").withRel("index"));
        resultResource.add(linkTo(ReservationApiController.class)
                .slash(reservationId).withSelfRel());
        resultResource.add(linkTo(ReservationApiController.class)
                .slash(reservationId)
                .withRel("update-reservation"));
        resultResource.add(new Link("/docs/index").withRel("profile"));

        return ResponseEntity.ok().body(resultResource);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity findAllReservation(@RequestBody @Valid ForFindPagingDto forFindPagingDto,
                                             Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        Page<Reservation> allReservation = reservationService.findAll(forFindPagingDto.getPageNum());
        if (allReservation.isEmpty())
            return ResponseEntity.noContent().build();

        List<ReservationResultApiDto> tempDtos = new ArrayList<>();
        for (Reservation r : allReservation.getContent()) {
            tempDtos.add(modelMapper.map(r,ReservationResultApiDto.class));
        }
        PageDto pageDto = new PageDto(forFindPagingDto.getPageNum(), 10,
                allReservation.getTotalElements(),allReservation.getTotalPages());

        ReservationsResultDto result = ReservationsResultDto.builder()
                .reservations(tempDtos)
                .pageDto(pageDto)
                .build();
        EntityModel reservationResource = EntityModel.of(result);
        reservationResource.add(new Link("/").withRel("index"));
        reservationResource.add(new Link("/docs/index").withRel("profile"));
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

        Reservation reservation = reservationService.findOneReservation(id);
        if (reservation == null)
            return ResponseEntity.noContent().build();

        if (reservation.getReserveSeat() != null){
            airlineService.subSeatCount(reservationRemoveDto.getAirlineId(), reservationRemoveDto.getTotalPerson());
            seatService.cancleSeats(reservationRemoveDto.getAirlineId(), reservation.getReserveSeat());
            reservationService.remove(reservation);
        } else {
            reservationService.remove(reservation);
        }

        Link selfLink = linkTo(ReservationApiController.class).slash(id).withSelfRel();
        Link indexLink = new Link("/").withRel("index");
        Link profileLink = new Link("/docs/index").withRel("profile");

        LinkDto linkDto = LinkDto.builder()
                .selfLink(selfLink)
                .indexLink(indexLink)
                .profileLink(profileLink)
                .build();

        return ResponseEntity.ok().body(linkDto);
    }

    /**
     * 예약 api완료, 리펙토링 요소는 일단 null값이 있는 경우의 예외처리. 예외를 reservationNullException
     * 이런 식으로 만들어줄것.+ 권한처리 필요한 메서드 @PreAuthorize("hasRole('ROLE_ADMIN')")해줄것
     * */
}
