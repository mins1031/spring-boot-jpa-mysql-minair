package com.minair.minair.domain.dto;

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
@AllArgsConstructor
@NoArgsConstructor
public class ReservationInfoResultDto {

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

}
