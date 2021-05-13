package com.minair.minair.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    @NotEmpty
    private Long goAirId;
    @NotEmpty
    private Long backAirId;
    @NotEmpty
    private String username;
    @NotEmpty
    private int adultCount;
    @NotEmpty
    private int childCount;
    @NotEmpty
    private int totalPerson;
    @NotEmpty
    private int totalPrice;
}
