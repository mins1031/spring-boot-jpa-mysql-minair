package com.minair.minair.controller;

import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.seat.SeatDtoForCheckIn;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;
    private final ModelMapper modelMapper;

    @GetMapping("/checkin")
    public void checkIn(@RequestParam("airlineId") Long airlineId,
                        @RequestParam("reservationId") Long reservationId,
                        @RequestParam("totalPerson") int totalPerson,
                        Model model){
        if (airlineId == null || totalPerson == 0 || reservationId == null)
            throw new RequestNullException();

        List<Seat> seats = seatService.checkInList(airlineId);
        List<SeatDtoForCheckIn> convertSeats = new ArrayList<>();
        for (Seat s :seats) {
            convertSeats.add(modelMapper.map(s,SeatDtoForCheckIn.class));
        }

        model.addAttribute("totalPerson", totalPerson);
        model.addAttribute("reservation", reservationId);
        model.addAttribute("airlineId", airlineId);

        if (seats != null)
            model.addAttribute("seatList",convertSeats);
        else
            model.addAttribute("seatList",null);
    }

}
