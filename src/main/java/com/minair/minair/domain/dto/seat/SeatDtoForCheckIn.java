package com.minair.minair.domain.dto.seat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatDtoForCheckIn {

    private Long id;
    private String seatName;
    private boolean seatStatus;
}
