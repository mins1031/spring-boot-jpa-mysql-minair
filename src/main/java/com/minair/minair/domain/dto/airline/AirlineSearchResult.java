package com.minair.minair.domain.dto.airline;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class AirlineSearchResult <T> {
    private T airlineResult ;

}
