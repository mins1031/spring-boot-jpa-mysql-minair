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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AirlineDto {

    private Long id;
    private Departure departure;
    private Distination distination;
    private LocalDate depart_date;
    private LocalTime depart_time;
    private LocalTime reach_time;
    private int aboveseat;
}
