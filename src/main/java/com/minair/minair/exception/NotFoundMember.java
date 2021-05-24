package com.minair.minair.exception;

public class NotFoundMember extends RuntimeException{
    @Override
    public String getMessage() {
        return "해당 회원을 찾을 수 없습니다.";
    }
}
