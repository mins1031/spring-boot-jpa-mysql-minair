package com.minair.minair.controller;

import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;

    @GetMapping("/index")
    public void adminIndex(){
        log.info("어드민 페이지");
    }

    @GetMapping("/new")
    public void joinAdmin(){
        log.info("어드민 계정 생성 페이지");
    }

    @PostMapping("/new")
    public String joinAdmin(@ModelAttribute("memberjoinDto") MemberJoinDto memberJoinDto){
        log.info("어드민 계정 등록 요청");
        try {
            memberService.join(memberJoinDto);
            return "redirect:/admin/index";
        } catch (RuntimeException e){
            e.printStackTrace();
        }
        return null;
    }
}
