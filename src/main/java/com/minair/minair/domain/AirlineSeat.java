package com.minair.minair.domain;

import com.minair.minair.domain.notEntity.SeatStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AirlineSeat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AirlineSeat_id")
    private Long id;

    /*@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "airline_id")
    private Airline airline;
*/
  /*  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "seat_id")
    private Seat seat;
*/
    private boolean seatStatus;

   /* public void setAirline(Airline airline){
        this.airline = airline;
        airline.getAirlineSeats().add(this);
    }
    public void setSeat(Seat seat){
        this.seat = seat;
    }

    public static AirlineSeat createAirlineSeat(Airline airline,Seat seat){
        AirlineSeat airlineSeat = new AirlineSeat();
        airlineSeat.setAirline(airline);
        airlineSeat.setSeat(seat);
        airlineSeat.seatStatus = false;

        return airlineSeat;
    }
*/
}
