package com.minair.minair.domain.dto.reservation;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ReservationResultApiDto {

    private Long id;
    private Long memberId;
    private Long airlineId;
    private Departure departure;
    private Distination distination;
    private LocalDate depart_date;
    private LocalTime depart_time;
    private int totalPerson;
    private int totalPrice;
    private String reserveSeat;

    public ReservationResultApiDto(Long id, Member member, Airline airline,
                                   int totalPerson, int totalPrice, String reserveSeat) {
        this.id = id;
        this.memberId = member.getId();
        this.airlineId = airline.getId();
        this.departure = airline.getDeparture();
        this.distination = airline.getDistination();
        this.depart_date = airline.getDepartDate();
        this.depart_time = airline.getDepartTime();
        this.totalPerson = totalPerson;
        this.totalPrice = totalPrice;
        this.reserveSeat = reserveSeat;
    }
}
