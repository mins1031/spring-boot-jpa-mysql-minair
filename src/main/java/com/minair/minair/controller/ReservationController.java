package com.minair.minair.controller;

import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.ReservationGenerateDto;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.reservation.*;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.repository.ReservationRepository;
import com.minair.minair.service.AirlineService;
import com.minair.minair.service.ReservationService;
import com.minair.minair.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservation/*")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationRepository reservationRepository;
    private final SeatService seatService;
    private final AirlineService airlineService;


    @PostMapping("/complete")
    public void reserve(@ModelAttribute("ReservationDto") @Valid ReservationDto reservationDto,
                        BindingResult bindingResult, Model model){

        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("예약을 위한 파라미터가 재대로 입력되지 않았습니다");
        }
        List<ReservationResultDto> resultList = reservationService.reservation(reservationDto);
        if (resultList == null)
            throw new NullPointerException();

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
            ReservationsResultDto reservations = reservationService.findReservations(username,pageNum);
            System.out.println(reservations.getReservations());
            model.addAttribute("reservations",reservations.getReservations());
            model.addAttribute("pageMaker",reservations.getPageDto());
            model.addAttribute("Empty",null);
        } catch (NullPointerException e){
            String messege = "아직 예약하신 상품이 없습니다.";
            model.addAttribute("Empty",messege);
        }
    }

    @PostMapping("/checkIn")
    public String checkIn(@ModelAttribute("checkIn") @Valid CheckInRegDto checkInRegDto,
                          Errors errors){

        if (errors.hasErrors())
            throw new NullPointerException();

        // 객체지향의 원칙에 따라
        // 예약에서 체크인 내용에 추가하는건 예약 서비스에서, 항공id통해서 좌석 상태 변경은 좌석서비스 통해서.
        reservationService.checkSeat(checkInRegDto.getReservationId(),checkInRegDto.getSelectSeats());
        seatService.checkInSeats(checkInRegDto.getAirlineId(), checkInRegDto.getSelectSeats());
        airlineService.subSeatCount(checkInRegDto.getAirlineId(), checkInRegDto.getTotalPerson());
        return "redirect:/";
    }

    @GetMapping("/info")
    public void confirmInfo(@RequestParam("reservationId") Long reservationId,
                            Model model){
        if (reservationId == null)
            throw new NullPointerException();

        ReservationDetailInfoDto reservationDetailInfoDto =
                reservationService.findOneReservation(reservationId);
        if (reservationDetailInfoDto == null)
            throw new NullPointerException();

        model.addAttribute("reservation", reservationDetailInfoDto);
    }

    @GetMapping("/all")
    public void allReservation(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                               Model model){
        if (pageNum == 0)
            throw new RequestNullException();

        log.info("예약 목록");
        ReservationsResultDto allReservation = reservationService.findAll(pageNum);

        model.addAttribute("reservations",allReservation.getReservations());
        model.addAttribute("pageMaker",allReservation.getPageDto());
    }

    @PostMapping("/remove")
    public String remove(@RequestParam("reservationId") Long reservationId,
                         @RequestParam("totalPerson") int totalPerson,
                         @RequestParam("airlineId") Long airlineId){

        if (reservationId == null)
            throw new RequestNullException();

//이로직들 서비스에 넣을것.
        try {
            Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
            Reservation findReservation = optionalReservation.get();
            if (findReservation.getReserveSeat() != null) {
                airlineService.plusSeatCount(findReservation.getAirline().getId(),totalPerson);
                seatService.cancleSeats(airlineId, findReservation.getReserveSeat());
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
    /**
     * 삭제나 변경 같이 위험한 동작 같은경우 보안측면에서 더 신경써서 파라미터의 예약id와 유저id를 통해 해당 유저의
     * 예약 목록을 받아ㅇ와 목록안헤 해다 예약이 있다면 변경이나 삭제를 진행하는 로직도 괜찮을것 같다.
     * */
}
