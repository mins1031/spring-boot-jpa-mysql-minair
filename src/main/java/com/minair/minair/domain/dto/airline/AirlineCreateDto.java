package com.minair.minair.domain.dto.airline;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirlineCreateDto {

    @NotNull
    private Departure departure;
    @NotNull
    private Distination distination;
    @NotNull
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate depart_date;
    @NotNull
    //@DateTimeFormat(pattern = "HH:mm")
    private LocalTime depart_time;
    @NotNull
    //@DateTimeFormat(pattern = "HH:mm")
    private LocalTime reach_time;
}
