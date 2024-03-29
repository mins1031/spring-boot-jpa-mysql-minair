package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.airline.AirlineSearchDto;

import java.util.List;

public interface AirlineSearchRepository {

    List<Airline> searchResults(AirlineSearchDto airlineSearchDto);
}
