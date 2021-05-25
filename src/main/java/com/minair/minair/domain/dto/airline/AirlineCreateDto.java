package com.minair.minair.domain.dto.airline;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class AirlineCreateDto {

    private Departure departure;
    private Distination distination;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate depart_date;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime depart_time;
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime reach_time;
}
