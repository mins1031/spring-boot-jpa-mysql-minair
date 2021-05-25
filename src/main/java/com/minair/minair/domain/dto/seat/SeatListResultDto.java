package com.minair.minair.domain.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatListResultDto<T> {

    private List<T> seatList = new ArrayList<>();
    private Long airlineId;
    private Long reservationId;
    private int totalPerson;

}
