package com.minair.minair.repository;

import com.minair.minair.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long>{

    public List<Seat> findByAirline_Id(Long airlineId);

    public Seat findByAirline_IdAndSeatName(Long airlineId,String seatName);

}
