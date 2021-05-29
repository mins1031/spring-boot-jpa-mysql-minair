package com.minair.minair.domain.dto.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationCompleteResultDto <T> {

    private T goAirline;
    private T backAirline;
}
