package com.minair.minair.api;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.UUID;


/**
 * jwt토큰 관련 컨트롤러 (멤버에서 이전)
 * */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenApiController {

    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh/{username}")
    public ResponseEntity refresh(@PathVariable String username){
        log.info("issue refresh token");
        System.out.println("refresh"+username);
        RefreshTokenProperty r = new RefreshTokenProperty(
                UUID.randomUUID().toString(), new Date().getTime()
        );
        memberService.issueRefreshToken(username,r);//토큰 DB저장
        TokenDto tokenDto = TokenDto.builder()
                .token(jwtTokenProvider.createRefreshToken(r))
                .build();
        return ResponseEntity.ok().body(tokenDto);
    }

    //토큰 재발급 함수 refresh의 호출 대상
    @PostMapping("/reissue/{refreshToken}")
    public ResponseEntity reIssue(@PathVariable String refreshToken){

        log.info("reIssue");
        System.out.println("reIssue"+refreshToken);

        if (jwtTokenProvider.validateRefreshToken(refreshToken)) {

            Member member = memberService.reIssueRefreshToken(refreshToken);

            if (member != null) {
                String reIssueToken = jwtTokenProvider.createToken(member.getUsername(), member.getRole());
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

    @GetMapping("/tokenExpirationCheck/{accessToken}")
    public ResponseEntity tokenCheck(@PathVariable String accessToken){
        log.info("엑세스 토큰 유효기간 확인 요청");
        boolean result = jwtTokenProvider.validateToken(accessToken);
        //토큰의 유효기간 체크후 기한 남았으면 true / 기한 만료 되었으면 false

        if (result) //기한 유효시
            return new ResponseEntity(HttpStatus.OK);
        else //기한 만료시
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

}
