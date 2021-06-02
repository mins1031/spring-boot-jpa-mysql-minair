package com.minair.minair.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckInRegDto {

    @NotNull
    private Long reservationId;
    @NotNull
    private int totalPerson;
    @NotNull
    private Long airlineId;
    @NotNull
    private String selectSeats;
}
