package com.minair.minair.controller;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.*;
import com.minair.minair.domain.dto.member.MemberInfoDto;
import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.domain.dto.member.MemberListDto;
import com.minair.minair.domain.dto.member.MemberModifyDto;
import com.minair.minair.exception.MemberListException;
import com.minair.minair.exception.NotFoundMember;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
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
    public String joinWeb(@ModelAttribute("memberjoinDto") @Valid MemberJoinDto memberJoinDto,
                          BindingResult bindingResult){
        log.info("회원가입 요청!");
        if (bindingResult.hasErrors()) {
            log.info("valid error!!");
            throw new RequestNullException();
        }
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
        if (findMember == null){
            throw new NotFoundMember();
        }

        MemberInfoDto memberInfoDto =
                MemberInfoDto.memberInfoDto(findMember);
        model.addAttribute("memberInfo",memberInfoDto);
    }

    @GetMapping("/member/modify")
    public void memberModifyGet(@ModelAttribute("memberDto") MemberInfoDto memberInfoDto){
        log.info("회원 수정 페이지 전환");
        System.out.println(memberInfoDto);
        if (memberInfoDto == null)
            new NullPointerException();
    }

    @PostMapping("/member/modify")
    public String memberModifyPost(@ModelAttribute("memberModifyDto") MemberModifyDto memberModifyDto){
        log.info("회원 정보 수정 요청");
        memberService.updateMember(memberModifyDto);
        return "redirect:/";
    }

    @GetMapping("/member/members")
    public void memberList(Model model,
                           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum){
        log.info("회원 목록 조회");

        if (pageNum == 0){
            log.info("페이지 넘버가 0임. 페이징 오류");
            throw new RequestNullException();
        }
        Page<Member> memberList = memberService.findByAll(pageNum);
        if (memberList.getContent().isEmpty()){
            log.info("회원이 없거나 회원목록 조회 실패");
            throw new MemberListException();
        }
        List<MemberListDto> memberListDtos = memberList.getContent().stream()
                .map(m -> new MemberListDto(m.getId(),m.getUsername(),m.getEmail(),
                        m.getName_kor(),m.getPhone()))
                .collect(Collectors.toList());

        PageDto pageDto = new PageDto(pageNum,10,memberList.getTotalElements(),
                memberList.getTotalPages());

        model.addAttribute("memberList", memberListDtos);
        model.addAttribute("pageMaker", pageDto);
    }
}
