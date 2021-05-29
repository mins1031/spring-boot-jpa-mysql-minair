package com.minair.minair.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minair.minair.domain.date.DateEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@ToString(exclude = "airline")
public class Reservation extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    //@JsonIgnore
    private Airline airline;
    //양방향 관계로 걸려있는경우 한쪽에는 꼭 @JsonIgnore를 걸어줘야 무한루프 = stackoverflow가 안걸림.
    private int adultCount;
    private int childCount;
    private int totalPerson;
    private int totalPrice;

    private String reserveSeat = null;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return adultCount == that.adultCount && childCount == that.childCount && totalPerson == that.totalPerson && totalPrice == that.totalPrice && Objects.equals(id, that.id) && Objects.equals(member, that.member) && Objects.equals(airline, that.airline) && Objects.equals(reserveSeat, that.reserveSeat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, airline, adultCount, childCount, totalPerson, totalPrice, reserveSeat);
    }
}
