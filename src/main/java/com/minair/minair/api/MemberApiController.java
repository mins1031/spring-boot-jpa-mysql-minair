package com.minair.minair.api;

import com.minair.minair.auth.PrincipalDetails;
import com.minair.minair.common.ErrorResource;
import com.minair.minair.domain.Member;
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

        Member join ;
        try {
            join = memberService.join(member);
        } catch (RuntimeException e){
            e.printStackTrace();
            String errorMessage = "서버에러! "+ e.getMessage();
            return new ResponseEntity(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }
        MemberInfoDto resultMember = modelMapper.map(join,MemberInfoDto.class);
        EntityModel resource = EntityModel.of(resultMember);
        resource.add(linkTo(methodOn(MemberApiController.class).join(member,errors)).withSelfRel());
        resource.add(new Link("/").withRel("index"));
        resource.add(new Link("/docs/index").withRel("profile"));

        return ResponseEntity.ok().body(resource);
    }

    @GetMapping("/check-id/{idVal}")
    public ResponseEntity checkId(@PathVariable String idVal){
        System.out.println(idVal);
        int checkResult = memberRepository.checkId(idVal);
        String result;
        if (checkResult == 0)
            result = "YES";
        else
            result = "NO";

        System.out.println(result);
        return ResponseEntity.ok().body(result);
    }
    //@PathVariable 민감한 정보 url post 대체요망

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

        Member member = memberRepository.findByUsername(loginRequestDto.getUsername());
        if (!passwordEncoder.matches(loginRequestDto.getPassword(),member.getPassword())){
            String errorMessage = "not matche password!";
            log.info(errorMessage);
            return new ResponseEntity(errorMessage,HttpStatus.BAD_REQUEST);
        } else {
            log.info("pw clean!");
            TokenDto tokenDto = TokenDto.builder()
                    .token(jwtTokenProvider.createToken(member.getUsername(),
                            member.getRoleList()))
                    .build();
            return ResponseEntity.ok().body(tokenDto);
        }
    }

    @GetMapping("/logout/{token}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity logout(@PathVariable("token") String refreshToken){
        log.info("로그아웃");

        PrincipalDetails principal = (PrincipalDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member principalMember = principal.getMember();
        Long memberId = principalMember.getId();

        boolean logoutCheck = memberService.logout(refreshToken);
        System.out.println(logoutCheck);
        String returnMessage ;
        if (logoutCheck) {
            returnMessage = "로그아웃!";
            return new ResponseEntity(returnMessage, HttpStatus.OK);
        } else {
            returnMessage = "로그아웃 실패! 새로고침해주세요.";
//            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(returnMessage);
            return new ResponseEntity(returnMessage,HttpStatus.UNAUTHORIZED);
        }
    }//

    //admin 페이지 진입시 admin계정인지 인증해주는 메서드
    @GetMapping("/checkAdmin/{username}")
    public ResponseEntity checkAdmin(@PathVariable String username){
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

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity findMember(@PathVariable String username){
        Member member = memberRepository.findByUsername(username);

        MemberInfoDto memberInfoDto =
                MemberInfoDto.memberInfoDto(member);

        EntityModel memberResource = EntityModel.of(memberInfoDto);
        memberResource.add(linkTo(methodOn(MemberApiController.class).findMember(username)).withSelfRel());
        memberResource.add(new Link("/").withRel("index"));
        memberResource.add(new Link("/docs/index").withRel("profile"));

        return ResponseEntity.ok().body(memberResource);
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity modifyMember(@RequestBody MemberModifyDto memberModifyDto){

        try {
            memberService.updateMember(memberModifyDto);
        } catch (RuntimeException e){
            e.printStackTrace();
            return new ResponseEntity(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Member m = memberService.findByUserInfo(memberModifyDto.getUsername());
        if (m == null)
            return ResponseEntity.noContent().build();

        MemberInfoDto memberInfoDto =
                MemberInfoDto.memberInfoDto(m);

        EntityModel memberResource = EntityModel.of(memberInfoDto);
        memberResource.add(new Link("/api/member/{username}").withRel("member-info"));
        memberResource.add(linkTo(MemberApiController.class).withSelfRel());
        memberResource.add(new Link("/").withRel("index"));
        memberResource.add(new Link("/docs/index").withRel("profile"));

        return ResponseEntity.ok().body(memberResource);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity findAllMember(@RequestBody @Valid ForFindPagingDto forFindPagingDto,
                                        Errors errors){
        if (errors.hasErrors())
            return ResponseEntity.badRequest().body(new ErrorResource(errors));

        Page<Member> members = memberService.findByAll(forFindPagingDto.getPageNum());
        if (members.getContent().isEmpty())
            return ResponseEntity.noContent().build();

        List<MemberListDto> memberListDtos = new ArrayList<>();
        for (Member m : members) {
            memberListDtos.add(modelMapper.map(m,MemberListDto.class));
        }

        PageDto pageDto = new PageDto(forFindPagingDto.getPageNum(), 10
                        ,members.getTotalElements(),members.getTotalPages());

        QueryMemberDto q = QueryMemberDto.builder()
                .memberList(memberListDtos)
                .pageDto(pageDto)
                .build();

        EntityModel memberResource = EntityModel.of(q);
        memberResource.add(new Link("/api/member/{username}").withRel("member-info"));
        memberResource.add(linkTo(MemberApiController.class).withSelfRel());
        memberResource.add(new Link("/").withRel("index"));
        memberResource.add(new Link("/docs/index").withRel("profile"));

        return ResponseEntity.ok().body(memberResource);
    }


    @DeleteMapping("/{username}")
    public boolean removeMember(@PathVariable String username){

        if (username == null)
            throw new RequestNullException();
        boolean deleteResult = memberService.delete(username);

        return deleteResult;
        //deleteResult가 false => 삭제 완료!
        //deleteResult가 true => 예약 먼저 삭제한후 탈퇴 해라!

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

}
