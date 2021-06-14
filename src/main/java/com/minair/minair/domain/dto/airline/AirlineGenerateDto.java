package com.minair.minair.domain.dto.airline;

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
@AllArgsConstructor
@Builder
public class AirlineGenerateDto {

    private Departure departure;
    private Distination distination;
    private LocalDate departDate;
    private LocalTime departTime;
    private LocalTime reachTime;

}
