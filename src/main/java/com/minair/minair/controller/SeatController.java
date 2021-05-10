package com.minair.minair.controller;

import com.minair.minair.domain.Seat;
import com.minair.minair.repository.SeatRepository;
import com.minair.minair.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @GetMapping("/checkin")
    public void checkIn(@RequestParam("airlineId") Long airlineId,
                        @RequestParam("reservationId") Long reservationId,
                        @RequestParam("totalPerson") int totalPerson,
                        Model model){
        if (airlineId == null || totalPerson == 0)
            log.info("항공권 번호나 인원 정보가 없습니다.");

        List<Seat> seats = seatService.checkInList(airlineId);

        System.out.println(totalPerson);
        System.out.println(reservationId);
        System.out.println(airlineId);

        model.addAttribute("totalPerson", totalPerson);
        model.addAttribute("reservation", reservationId);
        model.addAttribute("airlineId", airlineId);


        if (seats != null)
            model.addAttribute("seatList",seats);
        else
            model.addAttribute("seatList",null);
    }

}
