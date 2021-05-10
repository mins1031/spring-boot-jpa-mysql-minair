package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomReservationRepository {

    public Page<Reservation> pageReservations(Member member, Pageable pageable);

    Page<Reservation> pageAllReservation(Pageable pageable);
}
