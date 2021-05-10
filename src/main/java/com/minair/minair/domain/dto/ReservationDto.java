package com.minair.minair.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {

    private Long goAirId;
    private Long backAirId;
    private String username;
    private int adultCount;
    private int childCount;
    private int totalPerson;
    private int totalPrice;
}
