package com.minair.minair.domain.dto;

import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class AirlineSearchDto {

    private Departure departure;
    private Distination distination;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate depart_date;
    /*@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate comback_date;*/
    private int adult;
    private int child;

    public AirlineSearchDto(Departure departure,
                            Distination distination,
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

    /* @QueryProjection
    public AirlineSearchDto(Departure departure, Distination distination, LocalDate depart_date, int people) {
        this.departure = departure;
        this.distination = distination;
        this.depart_date = depart_date;
        this.people = people;
    }*/
}
