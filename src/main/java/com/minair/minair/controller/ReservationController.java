package com.minair.minair.controller;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.*;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.service.AirlineService;
import com.minair.minair.service.ReservationService;
import com.minair.minair.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservation/*")
public class ReservationController {

    private final ReservationService reservationService;
    private final SeatService seatService;
    private final AirlineService airlineService;

    @PostMapping("/complete")
    public void reserve(@ModelAttribute("ReservationDto") @Valid ReservationDto reservationDto,
                        BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors())
            throw new IllegalArgumentException();

        List<Reservation> reservationList = reservationService.reservation(reservationDto);
        if (reservationList == null)
            throw new NullPointerException();

        List<ReservationResultDto> resultList = reservationList.stream()
                .map(r -> new ReservationResultDto(r.getId(),r.getMember(),r.getAirline(),r.getAdultCount(),
                        r.getChildCount(),r.getTotalPerson(),r.getTotalPrice(),r.getReserveSeat()))
                .collect(Collectors.toList());
        model.addAttribute("goReserveList",resultList.get(0));
        model.addAttribute("backReserveList",resultList.get(1));
        //result 객체로 싸서 뷰로보내야함.
    }

    @GetMapping("/reservations")
    public void reservations(@RequestParam("username") String username,
                             @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, Model model){
        if (username == null) {
            log.info("id가 잘못 요청되었습니다.");
            throw new RequestNullException();
        }
        else if (pageNum == 0) {
            log.info("페이지 정보가 잘못 요청되었습니다.");
            throw new IllegalArgumentException();
        }
        try {
            Page<Reservation> reservations = reservationService.findReservation(username,pageNum);
            List<Reservation> teamList = reservations.getContent();
            List<ReservationResultDto> realList = teamList.stream()
                    .map(r -> new ReservationResultDto(r.getId(),r.getMember(),r.getAirline(),r.getAdultCount(),
                            r.getChildCount(),r.getTotalPerson(),r.getTotalPrice(),r.getReserveSeat()))
                    .collect(Collectors.toList());

            PageDto pageDto = new PageDto(pageNum,10,reservations.getTotalElements(),
                    reservations.getTotalPages());

            model.addAttribute("reservations",realList);
            model.addAttribute("pageMaker",pageDto);
            model.addAttribute("Empty",null);
        } catch (NullPointerException e){
            String messege = "아직 예약하신 상품이 없습니다.";
            model.addAttribute("Empty",messege);

        }
    }

    @PostMapping("/checkIn")
    public String checkIn(@RequestParam("reservationId") Long reservationId,
                        @RequestParam("totalPerson") int totalPerson,
                        @RequestParam("airlineId") Long airlineId,
                        @RequestParam("selectSeats") String selectSeats){

        if (reservationId == null || totalPerson == 0 || airlineId == null || selectSeats == null)
            throw new NullPointerException();

        // 객체지향의 원칙에 따라
        // 예약에서 체크인 내용에 추가하는건 예약 서비스에서, 항공id통해서 좌석 상태 변경은 좌석서비스 통해서.
        reservationService.checkSeat(reservationId,selectSeats);
        seatService.checkInSeats(airlineId,selectSeats);
        airlineService.subSeatCount(airlineId,totalPerson);
        return "redirect:/";
    }

    @GetMapping("/info")
    public void confirmInfo(@RequestParam("reservationId") Long reservationId,
                            Model model){
        if (reservationId == null)
            throw new NullPointerException();

        Reservation findReservation = reservationService.findOneReservation(reservationId);
        if (findReservation == null)
            throw new NullPointerException();
        ReservationDetailInfoDto reservationDetailInfoDto
                = ReservationDetailInfoDto.ReservationDetailInfoDto(findReservation);
        log.info("test 예약 정보");
        model.addAttribute("reservation", reservationDetailInfoDto);
        //이것도 dto로 리펙토링때 수정할것.
    }

    @GetMapping("/all")
    public void allReservation(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               Model model){
        if (pageNum == 0)
            throw new RequestNullException();

        log.info("예약 목록");
        Page<Reservation> allReservation = reservationService.findAll(pageNum);

        List<ReservationResultDto> resultList = allReservation.getContent().stream()
                .map(r -> new ReservationResultDto(r.getId(),r.getMember(),
                        r.getAirline(),r.getAdultCount(),r.getChildCount(),
                        r.getTotalPerson(),r.getTotalPrice(),r.getReserveSeat()))
                .collect(Collectors.toList());

        PageDto pageDto = new PageDto(pageNum,10,allReservation.getTotalElements(),
                allReservation.getTotalPages());

        model.addAttribute("reservations",resultList);
        model.addAttribute("pageMaker",pageDto);
    }

    @PostMapping("/remove")
    public String remove(@RequestParam("reservationId") Long reservationId,
                         @RequestParam("totalPerson") int totalPerson){
        if (reservationId == null)
            throw new RequestNullException();

        try {
            Reservation findReservation = reservationService.findOneReservation(reservationId);
            if (findReservation.getReserveSeat() != null) {
                airlineService.plusSeatCount(findReservation.getAirline().getId(),totalPerson);
                reservationService.remove(findReservation);
            } else
                reservationService.remove(findReservation);

            return "redirect:/";
        } catch (RuntimeException e){
            log.info(e.getMessage());
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
