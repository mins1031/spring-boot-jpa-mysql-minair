package com.minair.minair.domain.dto;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class AirlineDto {

    private Long id;
    private Departure departure;
    private Distination distination;
    private LocalDate depart_date;
    private LocalTime depart_time;
    private LocalTime reach_time;
    private int aboveseat;
}
