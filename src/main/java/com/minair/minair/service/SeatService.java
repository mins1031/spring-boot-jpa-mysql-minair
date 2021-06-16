package com.minair.minair.service;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Seat;
import com.minair.minair.domain.dto.seat.SeatDtoForCheckIn;
import com.minair.minair.repository.AirlineRepository;
import com.minair.minair.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;
    private final AirlineRepository airlineRepository;
    private final ModelMapper modelMapper;


    @Transactional
    public void createSeats(Airline airline, int createCount){
        String seatCode;
        String seatName;
        for (int i=0;i< 3;i++){
            if (i == 0){
                seatCode = "A";
            } else if (i == 1){
                seatCode = "B";
            } else {
                seatCode = "C";
            }
            for (int j =1;j<7;j++) {
                seatName = seatCode + j;//
                Seat seat = Seat.createSeat(seatName);
                seat.setAirline(airline);
                seatRepository.save(seat);
            }
        }
    }

    public List<Seat> checkInList(Long airlineId){
        System.out.println(airlineId);
        List<Seat> findSeats = seatRepository.findByAirline_Id(airlineId);
        if (findSeats != null)
            return findSeats;
        else
            return null;
    }

    @Transactional
    public List<SeatDtoForCheckIn> checkInSeats(Long airlineId, String seats){
        List<String> checkInList = Arrays.asList(seats.split(","));
        List<Seat> seatList = new ArrayList<>();
        for (String s:checkInList) {
            System.out.println("parameter-seat="+s);
            Seat findSeat = seatRepository.findByAirline_IdAndSeatName(airlineId, s);
            seatList.add(findSeat);
            findSeat.checkInSeat();
        }

        List<SeatDtoForCheckIn> seatDtoList = new ArrayList<>();
        for (Seat s: seatList) {
            seatDtoList.add(modelMapper.map(s,SeatDtoForCheckIn.class));
        }

        return seatDtoList;
    }

    @Transactional
    public void cancleSeats(Long airlineId, String seats) throws RuntimeException{
        List<String> cancleList = Arrays.asList(seats.split(","));

        for (int i = 0 ; i < cancleList.size(); i++){
            Seat findSeat =
                    seatRepository.findByAirline_IdAndSeatName(airlineId, cancleList.get(i));
            System.out.println(findSeat);
            findSeat.cancleReservation();
        }
    }
}
