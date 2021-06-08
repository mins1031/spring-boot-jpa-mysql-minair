package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CustomReservationRepository {

    Page<Reservation> pageReservations(Member member, Pageable pageable);

    Page<Reservation> pageAllReservation(Pageable pageable);

    boolean findReservationNotOverDate(String username);
}
