package com.minair.minair.domain.dto.airline;

import lombok.Data;

@Data
public class AirlineSearchApiDto<T> {

    private T goAirlineList;
    private T backAirlineList;
    private int adult;
    private int child;
    private int totalPerson;



    public AirlineSearchApiDto(T goAirlineList, T backAirlineList, int adult, int child) {
        this.goAirlineList = goAirlineList;
        this.backAirlineList = backAirlineList;
        this.adult = adult;
        this.child = child;
        this.totalPerson = adult + child;
    }
}
