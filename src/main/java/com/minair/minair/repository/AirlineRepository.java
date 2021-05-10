package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirlineRepository extends JpaRepository<Airline, Long>,AirlineSearchRepository
        ,AirlineCustomRepository {

}
