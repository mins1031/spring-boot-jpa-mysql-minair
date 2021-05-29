package com.minair.minair.domain.dto.reservation;

import com.minair.minair.domain.dto.PageDto;
import lombok.Data;

import java.util.List;

@Data
public class ReservationsResultDto {

    private List<ReservationResultDto> reservations;
    private PageDto pageDto;

    public ReservationsResultDto(List<ReservationResultDto> reservations, PageDto pageDto) {
        this.reservations = reservations;
        this.pageDto = pageDto;
    }
}
