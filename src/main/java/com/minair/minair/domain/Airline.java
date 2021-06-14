package com.minair.minair.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minair.minair.domain.date.DateEntity;
import com.minair.minair.domain.dto.airline.AirlineGenerateDto;
import com.minair.minair.domain.dto.airline.AirlineProperty;
import com.minair.minair.domain.notEntity.Departure;
import com.minair.minair.domain.notEntity.Distination;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
@ToString(exclude = "seats")
@AllArgsConstructor
@Builder
public class Airline extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airline_id")
    private Long id;

    @OneToMany(mappedBy = "airline")
    @JsonIgnore
    private List<Seat> seats = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Departure departure;

    @Enumerated(EnumType.STRING)
    private Distination distination;

    private LocalDate departDate;
    private LocalTime departTime;
    private LocalTime reachTime;

    private int seatCount;
    private int aboveSeat;

    public static Airline createAirline(AirlineGenerateDto airlineGenerateDto){

        AirlineProperty property = new AirlineProperty();

        return Airline.builder()
                .departure(airlineGenerateDto.getDeparture())
                .distination(airlineGenerateDto.getDistination())
                .departDate(airlineGenerateDto.getDepartDate())
                .departTime(airlineGenerateDto.getDepartTime())
                .reachTime(airlineGenerateDto.getReachTime())
                .seatCount(property.getLocationSeats())
                .aboveSeat(property.getLocationSeats())
                .build();
    }

    public void updateAirline(Airline airline){
        this.departure = airline.departure;
        this.distination = airline.distination;
        this.departDate = airline.departDate;
        this.departTime = airline.departTime;
        this.reachTime = airline.reachTime;
        this.seatCount = airline.seatCount;
        this.aboveSeat = airline.aboveSeat;
    }//만약에 항공권을 수정한다면 수정하는 화면을 생각했을때 기존 데이터를 바꿔 입력하는 형식으로 진행되기 때문에
    //폼으로 전체 엔티티값들이 왔다갔다함으로 값을 전부 바꿔주는 위 로직이 맞는듯

    public void discountSeat(int discount){
       if (this.aboveSeat - discount <= 0 || this.aboveSeat <= 0)
           new IllegalAccessError("남은 좌석이 없습니다.");
       else
        this.aboveSeat -=  discount;
    }

    public void plusSeat(int plusCount){
        if (this.aboveSeat + plusCount <= this.seatCount || this.aboveSeat == this.seatCount)
            new IllegalAccessError("좌석오류입니다. 다시 확인해 주세요.");
        else
            this.aboveSeat +=  plusCount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airline airline = (Airline) o;
        return seatCount == airline.seatCount && aboveSeat == airline.aboveSeat && Objects.equals(id, airline.id) && Objects.equals(seats, airline.seats) && departure == airline.departure && distination == airline.distination && Objects.equals(departDate, airline.departDate) && Objects.equals(departTime, airline.departTime) && Objects.equals(reachTime, airline.reachTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seats, departure, distination, departDate, departTime, reachTime, seatCount, aboveSeat);
    }
}
