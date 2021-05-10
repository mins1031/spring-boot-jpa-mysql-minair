package com.minair.minair.controller;

import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.PageDto;
import com.minair.minair.domain.dto.ReservationDto;
import com.minair.minair.domain.dto.ReservationResultDto;
import com.minair.minair.domain.dto.ReservationsResultDto;
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
import org.springframework.web.bind.annotation.*;

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
    public void reserve(@ModelAttribute("ReservationDto") ReservationDto reservationDto,
                                     Model model){
        List<Reservation> reservationList = reservationService.reservation(reservationDto);
        List<ReservationResultDto> resultList = reservationList.stream()
                .map(r -> new ReservationResultDto(r.getId(),r.getMember(),r.getAirline(),r.getAdultCount(),
                        r.getChildCount(),r.getTotalPerson(),r.getTotalPrice(),r.getReserveSeat()))
                .collect(Collectors.toList());
        System.out.println(resultList);
        model.addAttribute("goReserveList",resultList.get(0));
        model.addAttribute("backReserveList",resultList.get(1));
        //result 객체로 싸서 뷰로보내야함.
    }

    @GetMapping("/reservations")
    public void reservations(@RequestParam("username") String username,
                             @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, Model model){
        System.out.println("C:username= "+username);
        Page<Reservation> reservations = reservationService.findReservation(username,pageNum);
        List<Reservation> teamList = reservations.getContent();
        List<ReservationResultDto> realList = teamList.stream()
                .map(r -> new ReservationResultDto(r.getId(),r.getMember(),r.getAirline(),r.getAdultCount(),
                        r.getChildCount(),r.getTotalPerson(),r.getTotalPrice(),r.getReserveSeat()))
                .collect(Collectors.toList());

        PageDto pageDto = new PageDto(pageNum,10,reservations.getTotalElements(),
                reservations.getTotalPages());

        System.out.println(reservations.getTotalElements());
        System.out.println(reservations.getTotalPages());
        model.addAttribute("reservations",realList);
        model.addAttribute("pageMaker",pageDto);

    }

    @PostMapping("/checkIn")
    public String checkIn(@RequestParam("reservationId") Long reservationId,
                        @RequestParam("totalPerson") int totalPerson,
                        @RequestParam("airlineId") Long airlineId,
                        @RequestParam("selectSeats") String selectSeats){
        System.out.println("c-reservationId="+reservationId);
        System.out.println("c-seats="+selectSeats);
        System.out.println("c-totalPerson="+totalPerson);
        System.out.println("c-airlineId="+airlineId);
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
        Reservation findReservation = reservationService.findOneReservation(reservationId);
        model.addAttribute("reservation", findReservation);
    }
}
