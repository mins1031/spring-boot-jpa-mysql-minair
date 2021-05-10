package com.minair.minair.domain.dto;

import com.minair.minair.domain.Reservation;
import lombok.AllArgsConstructor;
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
