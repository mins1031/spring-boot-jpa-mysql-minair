package com.minair.minair.api;

import com.minair.minair.auth.PrincipalDetails;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    @PostMapping("/member/join")
    public void join(@RequestBody @Valid MemberJoinDto member){
        String inputPw = member.getPassword();
        String encodePw = passwordEncoder.encode(inputPw);

        /*Member joinMember = Member.joinMember(member.getUsername(), encodePw,
                member.getName_kor(), member.getName_eng(), member.getEmail(), member.getPhone(),
                member.getBirth(),member.getGender());
        joinMember.investRole("ROLE_MEMBER");
        memberService.join(joinMember);*/
        //API회원가입으로 바꿀것임.
    }//04-16 회원가입 로직 완료 시큐리티 설정 덜됨 로그인과정으로 jwt설정해줘야함.

    @PostMapping("/checkId")
    public String checkId(@RequestParam(value = "idVal") String idVal){
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

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDto loginRequestDto){
        log.info("post login");
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

    @GetMapping("/logout")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity logout(@RequestParam("refreshToken") String refreshToken){
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

    @PostMapping("/refresh")
    public String refresh(@RequestParam(value = "username") String username){
        log.info("issue refresh token");
        System.out.println("refresh"+username);
        RefreshTokenProperty r = new RefreshTokenProperty(
                UUID.randomUUID().toString(), new Date().getTime()
        );
        memberService.issueRefreshToken(username,r);//토큰 DB저장
        return jwtTokenProvider.createRefreshToken(r);
    }

    //토큰 재발급 함수 refresh의 호출 대상
    @PostMapping("/reissue")
    public ResponseEntity reIssue(@RequestParam("refreshToken") String refreshToken){

        log.info("reIssue");
        System.out.println("reIssue"+refreshToken);

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {

            Member member = memberService.reIssueRefreshToken(refreshToken);

            if (member != null) {
                String reIssueToken = jwtTokenProvider.createToken(member.getUsername(), member.getRoleList());
                Authentication authentication = jwtTokenProvider.getAuthentication(reIssueToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                return new ResponseEntity(reIssueToken, HttpStatus.OK);
            } else {
                return new ResponseEntity("로그인 정보 확인불가! 다시 로그인 해주세요",
                        HttpStatus.UNAUTHORIZED);
            }
        } else
            return new ResponseEntity("기한만료!! 다시 로그인 해주세요",HttpStatus.FORBIDDEN);
    }

    @GetMapping("/tokenExpirationCheck")
    public ResponseEntity tokenCheck(@RequestParam("accessToken") String accessToken){
        log.info("엑세스 토큰 유효기간 확인 요청");
        boolean result = jwtTokenProvider.validateToken(accessToken);
        //토큰의 유효기간 체크후 기한 남았으면 true / 기한 만료 되었으면 false

        if (result) //기한 유효시
            return new ResponseEntity(HttpStatus.OK);
        else //기한 만료시
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/user/info")
    public ResponseEntity<MemberInfoDto> userInfo(@RequestParam("token") String token){

        Member findMember = memberService.findUserInfo(token);
        MemberInfoDto memberInfoDto = MemberInfoDto.memberInfoDto(findMember);

        return new ResponseEntity<>(memberInfoDto, HttpStatus.OK);
    }//이친구도 보류
    //검증시 필요한 값은 새 access토큰(id, roles),
    // 회원id와 동일한 값을 db에서 가져와 헤더의 refreshToken값과 비교
    //비교후 둘이 동일하다면 새 토큰 응답해줘야함.

    @GetMapping("/checkAdmin")
    public ResponseEntity checkAdmin(@RequestParam("username") String username){
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
}
