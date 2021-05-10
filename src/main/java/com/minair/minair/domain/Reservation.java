package com.minair.minair.domain;

import com.minair.minair.domain.date.DateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Reservation extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    private Airline airline;

    private int adultCount;
    private int childCount;
    private int totalPerson;
    private int totalPrice;

    private String reserveSeat;

    public static Reservation createReservation(Member member, Airline airline,
                                                int adultCount, int childCount,
                                                int totalPerson,int totalPrice){
        Reservation reservation = new Reservation();
        reservation.member = member;
        reservation.airline = airline;
        reservation.adultCount = adultCount;
        reservation.childCount = childCount;
        reservation.totalPerson = totalPerson;
        reservation.totalPrice = totalPrice;

        return reservation;
    }

    public void setSeats(String seats){
        this.reserveSeat = seats;
    }

}
