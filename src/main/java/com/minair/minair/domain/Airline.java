package com.minair.minair.domain;

import com.minair.minair.domain.date.DateEntity;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class Airline extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airline_id")
    private Long id;

    @OneToMany(mappedBy = "airline")
    private List<Seat> seats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Departure departure;

    @Enumerated(EnumType.STRING)
    private Distination distination;

    private LocalDate depart_date;
    private LocalTime depart_time;
    private LocalTime reach_time;

    private int seatcount;
    private int aboveseat;

    public static Airline createAirline(Departure departure, Distination distination,
                                        LocalDate depart_date, LocalTime depart_time,
                                        LocalTime reach_time, int seatcount){
        Airline airline = new Airline();
        airline.departure = departure;
        airline.distination = distination;
        airline.depart_date = depart_date;
        airline.depart_time = depart_time;
        airline.reach_time = reach_time;
        airline.seatcount = seatcount;
        airline.aboveseat = seatcount;
        //남은 좌석수의 초기값은 전체좌석수

        return airline;
    }

    public void updateAirline(Airline airline){
        this.departure = airline.departure;
        this.distination = airline.distination;
        this.depart_date = airline.depart_date;
        this.depart_time = airline.depart_time;
        this.reach_time = airline.reach_time;
        this.seatcount = airline.seatcount;
        this.aboveseat = airline.aboveseat;
    }//만약에 항공권을 수정한다면 수정하는 화면을 생각했을때 기존 데이터를 바꿔 입력하는 형식으로 진행되기 때문에
    //폼으로 전체 엔티티값들이 왔다갔다함으로 값을 전부 바꿔주는 위 로직이 맞는듯

    public void discountSeat(int discount){
       if (this.aboveseat - discount <= 0 || this.aboveseat <= 0)
           new IllegalAccessError("남은 좌석이 없습니다.");
       else
        this.aboveseat -=  discount;
    }

    public void plusSeat(int plusCount){
        if (this.aboveseat + plusCount <= this.seatcount || this.aboveseat == this.seatcount)
            new IllegalAccessError("좌석오류입니다. 다시 확인해 주세요.");
        else
            this.aboveseat +=  plusCount;
    }

    public void setSeat(List<Seat> seat){
        for (Seat s: seat) {
            this.seats.add(s);
            s.setAirline(this);
        }
    }

    public void updateDeparture(Departure departure){
        this.departure = departure;
    }
}
