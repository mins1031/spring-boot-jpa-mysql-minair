package com.minair.minair.controller;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.AirlineDto;
import com.minair.minair.domain.dto.MemberInfoDto;
import com.minair.minair.domain.dto.MemberJoinDto;
import com.minair.minair.domain.dto.MemberListDto;
import com.minair.minair.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/join")
    public void join(){
        log.info("회원가입 페이지");
    }

    @PostMapping("/join")
    public String joinWeb(@ModelAttribute("memberjoinDto") MemberJoinDto memberJoinDto){
        log.info("회원가입 요청!");
        System.out.println(memberJoinDto.getUsername());
        System.out.println(memberJoinDto.getPassword());
        System.out.println(memberJoinDto.getEmail());
        System.out.println(memberJoinDto.getBirth());
        System.out.println(memberJoinDto.getName_kor());
        System.out.println(memberJoinDto.getName_eng());
        System.out.println(memberJoinDto.getPhone());
        System.out.println(memberJoinDto.getGender());
        memberService.join(memberJoinDto);
        return "redirect:/";
    }

    @GetMapping("/member/index")
    @Secured("ROLE_MEMBER")
    public void userIndex(){
        log.info("UserIndex Page");
    }

    @GetMapping("/member/info")
    public void memberInfo(@RequestParam("username") String username,
                           Model model){
        if (username == null){
            new NullPointerException();
        }

        Member findMember = memberService.findByUserInfo(username);
        //역시 엔티티를 dto로 바꿔줘야함.
        MemberInfoDto memberInfoDto =
                MemberInfoDto.memberInfoDto(findMember);
        model.addAttribute("memberInfo",memberInfoDto);
    }

    @GetMapping("/member/modify")
    public void memberModify(@ModelAttribute("memberDto")MemberInfoDto memberInfoDto,
                             Model model){
        log.info("회원 수정 페이지 전환");
        System.out.println(memberInfoDto);
        if (memberInfoDto == null)
            new NullPointerException();

        model.addAttribute("memberInfo", memberInfoDto);
    }

    @GetMapping("/member/members")
    public void memberList(Model model){
        log.info("회원 목록 조회");
        List<Member> memberList = memberService.findByAll();
        List<MemberListDto> memberListDtos = memberList.stream()
                .map(m -> new MemberListDto(m.getId(),m.getUsername(),m.getEmail(),
                        m.getName_kor(),m.getPhone()))
                .collect(Collectors.toList());
        model.addAttribute("memberList", memberListDtos);
    }
}
