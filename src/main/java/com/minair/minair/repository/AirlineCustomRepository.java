package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AirlineCustomRepository {

    Page<Airline> allAirlineList(Pageable pageable);
}
