package com.minair.minair.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    @NotNull
    private Long goAirId;
    @NotNull
    private Long backAirId;
    @NotBlank
    private String username;
    @NotNull
    @Max(5)
    private int adultCount;
    @NotNull
    @Max(5)
    private int childCount;
    @Max(18)
    @NotNull
    private int totalPerson;
    @NotNull
    private int totalPrice;
}
