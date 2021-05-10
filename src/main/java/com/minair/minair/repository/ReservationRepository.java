package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,CustomReservationRepository {

    public List<Reservation> findAllByMember(Member member);
}
