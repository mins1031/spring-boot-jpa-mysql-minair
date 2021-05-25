package com.minair.minair.domain.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckInDto {

    @NotNull
    private Long airlineId;
    @NotNull
    private Long reservationId;
    @Min(0)
    @Max(10)
    private int totalPerson;
}
