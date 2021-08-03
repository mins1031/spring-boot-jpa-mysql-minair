package com.minair.minair.api;

import com.minair.minair.auth.PrincipalDetails;
import com.minair.minair.common.BasicResource;
import com.minair.minair.common.ErrorResource;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.dto.common.ForFindPagingDto;
import com.minair.minair.domain.dto.member.LoginRequestDto;
import com.minair.minair.domain.dto.common.PageDto;
import com.minair.minair.domain.dto.member.*;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.exception.RequestNullException;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/member")
public class MemberApiController {

    private final JwtTokenProvider jwtTokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final ModelMapper modelMapper;

    @PostMapping("/new")
    public ResponseEntity join(@RequestBody @Valid MemberJoinDto member,Errors errors){

        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        try {

            MemberInfoDto join = memberService.join(member);

            BasicResource basicResource = new BasicResource(join);
            basicResource.add(linkTo(methodOn(MemberApiController.class).join(member,errors)).withSelfRel());

            return ResponseEntity.ok().body(basicResource);
        } catch (RuntimeException e){
            e.printStackTrace();
            String errorMessage = "서버에러! "+ e.getMessage();
            return new ResponseEntity(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    /**
     * 회원가입 로직:
     * joinDto -> if valid걸리면 에러리턴 ,
     * */

    @GetMapping("/check-id/{idVal}")
    public ResponseEntity checkId(@PathVariable String idVal){
        int checkResult = memberRepository.checkId(idVal);
        boolean result;
        if (checkResult == 0)
            result = true;
        else
            result = false;

        return ResponseEntity.ok().body(result);
    }
    //@PathVariable 민감한 정보 url post 대체요망 => 조금더 고민해보장

/**
 * 프로젝트 자체를 분리...하는거 보다는 웹과 api를 컨트롤러만 분리해주면 될것같았는데 음.........
 * 멤버에서 막혀버림. 이미 멤버의 일부 기능들은 rest로 구현이 되어있는데 이것 들을 포함해서 api컨트롤러를 만들어야할지.
 * 1) 포함해서 새로 만든다. => 시간 오래걸림, 코드중복이 제일 걸려버림. 구지 같은요청에 같은 응답을 주는 코드가 있는데
 * 이걸 중복해서 사용하는 것도 말이 안됨.
 * 2) 갈무리해서 그대로 사용할것 사용하고 아닌건 새로 만들기 => 이게 베스트...인듯?, 토큰에 대한 api는 따로 빼주고
 * 웹에서 ajax로 사용하던 내역들 모두 앞에 '/api' 정도만 붙혀주면 될듯
 * */

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginRequestDto loginRequestDto, Errors errors){
        log.info("post login");


        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        LoginServiceDto loginResult = memberService.login(loginRequestDto);
        if (loginResult.isIdNotMatch()){
            return ResponseEntity.badRequest().body(1000);
        } else if (loginResult.isWrongPwd())
            return ResponseEntity.badRequest().body(1001);

        Member member = memberRepository.findByUsername(loginRequestDto.getUsername());

        TokenDto tokenDto = TokenDto.builder()
                .token(jwtTokenProvider.createToken(member.getUsername(),
                        member.getRole()))
                .build();
        return ResponseEntity.ok().body(tokenDto);
    }

    @GetMapping("/logout/{username}")
    @PreAuthorize("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity logout(@PathVariable String username){
        log.info("로그아웃");

        boolean logoutCheck = memberService.logout(username);
        String returnMessage ;
        if (logoutCheck) {
            returnMessage = "로그아웃!";
            return new ResponseEntity(returnMessage, HttpStatus.OK);
        } else {
            returnMessage = "로그아웃 실패! 새로고침해주세요.";
            return new ResponseEntity(returnMessage, HttpStatus.UNAUTHORIZED);
        }
    }//

    //admin 페이지 진입시 admin계정인지 인증해주는 메서드
    @GetMapping("/check-admin/{username}")
    public ResponseEntity checkAdmin(@PathVariable String username){
        Member findUsername = memberRepository.findByUsername(username);
        //단순 조회기 때문에 컨트롤러 -> 리포지토리로 바로 조회

        if (MemberRole.ROLE_ADMIN == findUsername.getRole())
            return new ResponseEntity(HttpStatus.OK);

        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }

    //회원 단건 조회
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity findMember(@PathVariable String username){
        Member member = memberRepository.findByUsername(username);
        if (member == null)
            return new ResponseEntity("존재하지 않는 회원입니다. 로그인 해주세요",HttpStatus.BAD_REQUEST);

        MemberInfoDto memberInfoDto =
                MemberInfoDto.memberInfoDto(member);

        BasicResource memberResource = new BasicResource(memberInfoDto);
        memberResource.add(linkTo(methodOn(MemberApiController.class).findMember(username)).withSelfRel());
        return ResponseEntity.ok().body(memberResource);
    }

    //회원 수정
    @PutMapping
    @PreAuthorize("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity modifyMember(@RequestBody MemberModifyDto memberModifyDto){
        //변경이기 때문에 validation은 x. 또한 @PreAuthorize로 필터에서 기본적인 인증이 이루어지기 때문에 요청값 유효성 검사는 x
        //update의 경우 민감할수 있는 id,password는 따로 변경 루트를 제공하고 편하게 바뀌어도 되는 부분들을 변경한다.
        try {
            memberService.updateMember(memberModifyDto);
        } catch (RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
        //update시 혹여나 발생할 예외를 대비
        MemberInfoDto memberInfoDto = memberService.findByUserInfo(memberModifyDto.getUsername());
        if (memberInfoDto == null)
            return ResponseEntity.noContent().build();
        BasicResource basicResource = new BasicResource(memberInfoDto);
        basicResource.add(linkTo(MemberApiController.class).withSelfRel());
        basicResource.add(new Link("/api/member/{username}").withRel("member-info"));

        return ResponseEntity.ok().body(basicResource);
    }

    //RequestBody 바꿔야됨
    @GetMapping
    @PreAuthorize("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity findAllMember(@RequestParam(value = "pageNum",defaultValue = "1")@NotNull int pageNum){
        if (pageNum == 0)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        QueryMemberDto queryMemberDto = memberService.findByAll(pageNum);
        if (queryMemberDto.getMemberList() == null)
            return ResponseEntity.noContent().build();

        BasicResource basicResource = new BasicResource(queryMemberDto);
        basicResource.add(linkTo(MemberApiController.class).withSelfRel());
        basicResource.add(new Link("/api/member/{username}").withRel("member-info"));
        return ResponseEntity.ok().body(basicResource);
    }


    @DeleteMapping("/{username}")
    public boolean removeMember(@PathVariable String username){

        if (username == null)
            throw new RequestNullException();
        boolean deleteResult = memberService.delete(username);

        return deleteResult;
        //deleteResult가 false => 삭제 완료!
        //deleteResult가 true => 예약 먼저 삭제한후 탈퇴 해라!
        //delete는 db관계 해결후 다시 정의 할것.
    }

}
