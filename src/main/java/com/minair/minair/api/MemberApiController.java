package com.minair.minair.api;

import com.minair.minair.auth.PrincipalDetails;
import com.minair.minair.common.ErrorResource;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.LoginRequestDto;
import com.minair.minair.domain.dto.MemberInfoDto;
import com.minair.minair.domain.dto.MemberJoinDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberApiController {

    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @PostMapping("/api/member/new")
    public ResponseEntity join(@RequestBody @Valid MemberJoinDto member,Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        Member join = memberService.join(member);
        if (join == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity.ok().body(join);
    }

    @GetMapping("/api/member/checkId/{idVal}")
    public String checkId(@PathVariable String idVal){
        System.out.println(idVal);
        int checkResult = memberRepository.checkId(idVal);
        String result;
        if (checkResult == 0)
            result = "YES";
        else
            result = "NO";

        System.out.println(result);
        return result;
    }
/**
 * 프로젝트 자체를 분리...하는거 보다는 웹과 api를 컨트롤러만 분리해주면 될것같았는데 음.........
 * 멤버에서 막혀버림. 이미 멤버의 일부 기능들은 rest로 구현이 되어있는데 이것 들을 포함해서 api컨트롤러를 만들어야할지.
 * 1) 포함해서 새로 만든다. => 시간 오래걸림, 코드중복이 제일 걸려버림. 구지 같은요청에 같은 응답을 주는 코드가 있는데
 * 이걸 중복해서 사용하는 것도 말이 안됨.
 * 2) 갈무리해서 그대로 사용할것 사용하고 아닌건 새로 만들기 => 이게 베스트...인듯?, 토큰에 대한 api는 따로 빼주고
 * 웹에서 ajax로 사용하던 내역들 모두 앞에 '/api' 정도만 붙혀주면 될듯
 * */

    @PostMapping("/api/member/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDto loginRequestDto, Errors errors){
        log.info("post login");

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        Member member = memberRepository.findByUsername(loginRequestDto.getUsername());
        if (!passwordEncoder.matches(loginRequestDto.getPassword(),member.getPassword())){
            System.out.println("비밀번호가 일치하지 않습니다.");
            return new ResponseEntity(null,HttpStatus.BAD_REQUEST);
        } else {
            log.info("pw clean!");
            return new ResponseEntity(jwtTokenProvider.createToken(member.getUsername()
                    , member.getRoleList()), HttpStatus.OK);
        }
    }

    @GetMapping("/api/member/logout/{token}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity logout(/*@RequestParam("refreshToken")*/@PathVariable("token") String refreshToken){
        log.info("로그아웃");
        if (refreshToken == null)
            return  new ResponseEntity(HttpStatus.UNAUTHORIZED);
        System.out.println(refreshToken);

        boolean logoutCheck = memberService.logout(refreshToken);
        System.out.println(logoutCheck);
        if (logoutCheck)
            return new ResponseEntity(HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    //admin 페이지 진입시 admin계정인지 인증해주는 메서드
    @GetMapping("/api/member/checkAdmin/{username}")
    public ResponseEntity checkAdmin(/*@RequestParam("username")*/@PathVariable String username){
        Member findUsername = memberRepository.findByUsername(username);
        List<String> roleList = findUsername.getRoleList();
        System.out.println(roleList);
        boolean result = false;
        String admin = "ROLE_ADMIN";
        for (String s: roleList) {
            System.out.println(s);
            if (s.equals(admin)) {
                System.out.println("!");
                result = true;
            }
        }
        if (result == true) {
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }



    //이 밑은 뭔지 모르는 메서드. 확인 안되면 지울것.
    @GetMapping("/user/info")
    public ResponseEntity<MemberInfoDto> userInfo(@RequestParam("token") String token){

        Member findMember = memberService.findUserInfo(token);
        MemberInfoDto memberInfoDto = MemberInfoDto.memberInfoDto(findMember);

        return new ResponseEntity<>(memberInfoDto, HttpStatus.OK);
    }//이친구도 보류
    //검증시 필요한 값은 새 access토큰(id, roles),
    // 회원id와 동일한 값을 db에서 가져와 헤더의 refreshToken값과 비교
    //비교후 둘이 동일하다면 새 토큰 응답해줘야함.
    //이거 일단 아무것도 없는 역할 없는 메서드임

    @GetMapping("/user/{username}")
    public ResponseEntity findMember(@PathVariable String username){
        Member member = memberRepository.findByUsername(username);

        MemberInfoDto memberInfoDto =
                MemberInfoDto.memberInfoDto(member);

        return ResponseEntity.ok().body(memberInfoDto);
    }//앤 뭐지..?
}
