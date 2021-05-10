package com.minair.minair.domain;

import com.minair.minair.domain.notEntity.SeatStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Seat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;
    private String seatName;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "airline_id")
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

}
