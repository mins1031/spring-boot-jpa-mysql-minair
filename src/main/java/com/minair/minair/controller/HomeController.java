package com.minair.minair.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String index(){
        log.info("index");
        return "index";
    }

    @GetMapping("/hello")
    public String hello(){
        log.info("hello");
        return "hello";
    }

    @GetMapping("/test")
    public void test(){
        log.info("test");
    }

    @GetMapping("/seat")
    public void reservation(){
        log.info("seat");
    }

    @GetMapping("/loginForm2")
    public void loginForm2(){
        log.info("로그인 페이지");
    }

    @GetMapping("/board")
    public void freeBoard(){
        log.info("자유게시판 요청");
    }

}
