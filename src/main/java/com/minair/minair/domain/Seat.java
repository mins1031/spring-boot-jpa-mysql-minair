package com.minair.minair.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minair.minair.domain.notEntity.SeatStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString(exclude = "airline")
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;
    private String seatName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "airline_id")
    @JsonIgnore
    private Airline airline;

    private boolean seatStatus;

    public static Seat createSeat(String seatName){
        Seat seat = new Seat();
        seat.seatName = seatName;
        seat.seatStatus = false;
        return seat;
    }

    public void setAirline(Airline airline){
        this.airline = airline;
        airline.getSeats().add(this);
    }

    public void checkInSeat(){
        this.seatStatus = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return seatStatus == seat.seatStatus && Objects.equals(id, seat.id) && Objects.equals(seatName, seat.seatName) && Objects.equals(airline, seat.airline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seatName, airline, seatStatus);
    }
}
