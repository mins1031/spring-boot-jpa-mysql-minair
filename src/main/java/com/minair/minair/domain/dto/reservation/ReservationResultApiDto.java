package com.minair.minair.domain.dto.reservation;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
public class ReservationResultApiDto {

    private Long id;
    private String username;
    private Long airlineId;
    private Departure departure;
    private Distination distination;
    private LocalDate departDate;
    private LocalTime departTime;
    private int totalPerson;
    private int totalPrice;
    private String reserveSeat;

    public ReservationResultApiDto(Reservation reservation) {

        this.id = reservation.getId();
        this.username = reservation.getMember().getUsername();
        this.airlineId = reservation.getAirline().getId();
        this.departure = reservation.getAirline().getDeparture();
        this.distination = reservation.getAirline().getDistination();
        this.departDate = reservation.getAirline().getDepartDate();
        this.departTime = reservation.getAirline().getDepartTime();
        this.totalPerson = reservation.getTotalPerson();
        this.totalPrice = reservation.getTotalPrice();
        this.reserveSeat = reservation.getReserveSeat();
    }
}
