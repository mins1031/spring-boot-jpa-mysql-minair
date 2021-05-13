package com.minair.minair.exception;

public class RequestNullException extends RuntimeException{
    @Override
    public String getMessage() {
        return "파라미터의 값이 Null입니다.";
    }
}
