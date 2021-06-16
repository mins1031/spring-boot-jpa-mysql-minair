package com.minair.minair.domain.dto.reservation;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResultDto {

    private Long id;
    private String username;
    private Long airlineId;
    private Departure departure;
    private Distination distination;
    private LocalDate departDate;
    private LocalTime departTime;
    private int totalPerson;
    private String reserveSeat;
}
