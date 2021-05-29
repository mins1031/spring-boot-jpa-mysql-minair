package com.minair.minair.domain.dto.reservation;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResultDto {

    private Long id;
    private Member member;
    private Airline airline;
    private int adultCount;
    private int childCount;
    private int totalPerson;
    private int totalPrice;
    private String reserveSeat;
}
