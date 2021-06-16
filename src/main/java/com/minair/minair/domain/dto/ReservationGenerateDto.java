package com.minair.minair.domain.dto;

import com.minair.minair.domain.Airline;
import com.minair.minair.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationGenerateDto {

    private Member member;
    private Airline airline;
    private int adultCount;
    private int childCount;
    private int totalPerson;
    private int totalPrice;

}
