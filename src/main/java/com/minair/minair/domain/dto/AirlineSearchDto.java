package com.minair.minair.domain.dto;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AirlineSearchDto {

    @NotNull
    private Departure departure;
    @NotNull
    private Distination distination;
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate depart_date;
    @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate comebackDate;
    @Max(5)
    private int adult;
    @Max(5)
    private int child;

    public AirlineSearchDto(Departure departure, Distination distination,
                            LocalDate depart_date, LocalDate comebackDate,
                            int adult, int child) {
        this.departure = departure;
        this.distination = distination;
        this.depart_date = depart_date;
        this.comebackDate = comebackDate;
        this.adult = adult;
        this.child = child;
    }

    public AirlineSearchDto(Departure departure, Distination distination,
                            LocalDate depart_date,
                            int adult, int child) {
        this.departure = departure;
        this.distination = distination;
        this.depart_date = depart_date;
        this.adult = adult;
        this.child = child;
    }


    public int getTotalPerson(){
        int totalPerson = this.adult + this.child;
        return totalPerson;
    }

}
