package com.minair.minair.controller;

import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.exception.PageNumberException;
import com.minair.minair.exception.RequestNullException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(RequestNullException.class)
    public String requestNullException(){
        return "/error/400";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(){
        return "/error/404";
    }

    @ExceptionHandler(PageNumberException.class)
    public String pageNumberException(){
        return "웹 페이지 정보 오류 입니다. 재시도해주세요.(재시도에도 오류가 지속된다면 관리자에게 문의해주세요) 라는" +
                "내용의 에러페이지 출력";
    }

    @ExceptionHandler(NotFoundAirlines.class)
    public String notFoundAirlines(){
        return "해당날짜에 항공권이 검색되지 않았습니다. 다른날짜를 선택해 주세요.라는 내용의 에러페이지 출력";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentException(){
        //return "잘못된 정보가 입력되었습니다. 입력한 정보를 다시 확인해 주세요 라는 내용의 에러 페이지 출력";\
        return "/error/400";
    }

    @ExceptionHandler(NullPointerException.class)
    public String nullPointerException(){
        //return "잘못된 정보가 입력되었습니다. 입력한 정보를 다시 확인해 주세요 라는 내용의 에러 페이지 출력";\
        return "/error/400";
    }


}
