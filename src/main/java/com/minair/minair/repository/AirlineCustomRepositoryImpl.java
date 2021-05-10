package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.QAirline;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityManager;
import java.util.List;

import static com.minair.minair.domain.QAirline.airline;

public class AirlineCustomRepositoryImpl implements AirlineCustomRepository {

    private final JPAQueryFactory queryFactory;

    public AirlineCustomRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Airline> allAirlineList(Pageable pageable) {
        List<Airline> airlineList = queryFactory
                .selectFrom(airline)
                .orderBy(airline.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(airline)
                .fetchCount();

        return new PageImpl<>(airlineList,pageable,total);
    }
}
