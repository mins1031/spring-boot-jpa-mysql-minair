package com.minair.minair.controller;

import com.minair.minair.exception.MemberListException;
import com.minair.minair.exception.NotFoundAirlines;
import com.minair.minair.exception.PageNumberException;
import com.minair.minair.exception.RequestNullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice("com.minair.minair.controller")
@Slf4j
public class AdviceController {

    @ExceptionHandler(RequestNullException.class)
    public String requestNullException(){
        log.info("RequestNullException!");
        return "/error/400";
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String noHandlerFoundException(){
        return "/error/404";
    }

    @ExceptionHandler(PageNumberException.class)
    public String pageNumberException(){
        return "/error/500";
    }

    @ExceptionHandler(NotFoundAirlines.class)
    public String notFoundAirlines(){
        log.info("NotFoundAirlines!");
        return "/error/500";
    }

    @ExceptionHandler(MemberListException.class)
    public String memberListException(){
        log.info("MemberListException!");
        return "/error/500";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String illegalArgumentException(){
        log.info("IllegalArgumentException!");
        return "/error/400";
    }

    @ExceptionHandler(NullPointerException.class)
    public String nullPointerException(){
        log.info("NullPointerException!");
        return "/error/400";
    }

    @ExceptionHandler(RuntimeException.class)
    public String runtimeException(){
        log.info("RuntimeException!");
        return "/error/500";
    }



}
