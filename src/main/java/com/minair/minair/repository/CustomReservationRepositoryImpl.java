package com.minair.minair.repository;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.QReservation;
import com.minair.minair.domain.Reservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

import static com.minair.minair.domain.QReservation.reservation;


public class CustomReservationRepositoryImpl implements CustomReservationRepository{

    private final JPAQueryFactory queryFactory;

    public CustomReservationRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Reservation> pageReservations(Member member, Pageable pageable) {
        List<Reservation> reservations =
                queryFactory
                    .selectFrom(reservation)
                    .where(reservation.member().eq(member))
                    .orderBy(reservation.id.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

        long total = queryFactory
                .selectFrom(reservation)
                .where(reservation.member().eq(member))
                .fetchCount();

        return new PageImpl<>(reservations,pageable,total);
    }

    @Override
    public Page<Reservation> pageAllReservation(Pageable pageable) {
        List<Reservation> reservations = queryFactory
                .selectFrom(reservation)
                .orderBy(reservation.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(reservation)
                .fetchCount();

        return new PageImpl<>(reservations,pageable,total);
    }

    @Override
    public boolean findReservationNotOverDate(String username) {
        LocalDate data = LocalDate.now();
        long fetchCount = queryFactory
                .selectFrom(reservation)
                .where(reservation.airline().departDate.after(data))
                .fetchCount();

        boolean result = false ;
        if (fetchCount > 0)
            result = true;

        return result;
    }
}
