package com.minair.minair.domain.dto.reservation;

import com.minair.minair.domain.dto.common.PageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationsResultDto <T>{

    private T reservations;
    private PageDto pageDto;
}
