package com.minair.minair.exception;

public class NotFoundAirlines extends RuntimeException{

    @Override
    public String getMessage() {
        return "해당 날짜에 남은 비행기가 없습니다. ";
    }
}
