package com.minair.minair.domain.dto;

import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@Builder
public class ReservationDetailInfoDto {

    private Long reservationId;
    private String username;
    private int adult;
    private int child;
    private Departure departure;
    private Distination distination;
    private LocalDate departDate;
    private LocalTime departTime;
    private LocalTime reachTime;
    private Long airlineId;
    private int totalPrice;
    private String reserveSeats;

    public static ReservationDetailInfoDto ReservationDetailInfoDto(Reservation reservation){
        return ReservationDetailInfoDto.builder()
                .reservationId(reservation.getId())
                .username(reservation.getMember().getUsername())
                .adult(reservation.getAdultCount())
                .child(reservation.getChildCount())
                .departure(reservation.getAirline().getDeparture())
                .distination(reservation.getAirline().getDistination())
                .departDate(reservation.getAirline().getDepart_date())
                .departTime(reservation.getAirline().getDepart_time())
                .reachTime(reservation.getAirline().getReach_time())
                .airlineId(reservation.getAirline().getId())
                .totalPrice(reservation.getTotalPrice())
                .reserveSeats(reservation.getReserveSeat())
                .build();
    }

}
