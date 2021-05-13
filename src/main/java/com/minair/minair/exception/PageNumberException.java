package com.minair.minair.exception;

public class PageNumberException extends RuntimeException{

    @Override
    public String getMessage() {
        return "페이지 정보가 잘못되었습니다. 다시 시도해 주세요.";
    }
}
