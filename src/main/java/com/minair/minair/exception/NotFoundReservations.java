package com.minair.minair.exception;

public class NotFoundReservations extends RuntimeException{

    @Override
    public String getMessage() {
        return "예약 내역이 없습니다. ";
    }
}
