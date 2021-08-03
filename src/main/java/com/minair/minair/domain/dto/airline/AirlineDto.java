package com.minair.minair.domain.dto.airline;

import com.minair.minair.domain.Airline;
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

    private Departure departure;
    private Distination distination;
    private LocalDate depart_date;
    private LocalTime depart_time;
    private LocalTime reach_time;
    private int aboveseat;

    public static AirlineDto airlineDto(Airline airline){

        return AirlineDto.builder()
                .departure(airline.getDeparture())
                .distination(airline.getDistination())
                .depart_date(airline.getDepartDate())
                .depart_time(airline.getDepartTime())
                .reach_time(airline.getReachTime())
                .aboveseat(airline.getAboveSeat())
                .build();
    }
}
