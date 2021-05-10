package com.minair.minair.repository;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.dto.AirlineSearchDto;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import java.util.List;

import static com.minair.minair.domain.QAirline.airline;

public class AirlineSearchRepositoryImpl implements AirlineSearchRepository{

    private final JPAQueryFactory queryFactory;

    public AirlineSearchRepositoryImpl(EntityManager em) {
        queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Airline> searchResults(AirlineSearchDto airlineSearchDto) {
        System.out.println(airlineSearchDto.getDeparture());
        System.out.println(airlineSearchDto.getDistination());
        System.out.println(airlineSearchDto.getDepart_date());
        System.out.println(airlineSearchDto.getAdult());
        System.out.println(airlineSearchDto.getChild());
        int totalPerson = airlineSearchDto.getAdult() + airlineSearchDto.getChild();


        return queryFactory
                .select(airline)
                .from(airline)
                .where(airline.departure.eq(airlineSearchDto.getDeparture())
                        .and(airline.distination.eq(airlineSearchDto.getDistination()))
                        .and(airline.depart_date.goe(airlineSearchDto.getDepart_date()))
                        .and(airline.aboveseat.goe(totalPerson)))
                .fetch();
    }
}
