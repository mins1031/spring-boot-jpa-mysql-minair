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

        TokenDto tokenDto = memberService.issueRefreshToken(username);//토큰 DB저장

        return ResponseEntity.ok().body(tokenDto);
    }

    //토큰 재발급 함수 refresh의 호출 대상
    @PostMapping("/reissue/{refreshToken}")
    public ResponseEntity reIssue(@PathVariable String refreshToken){

        log.info("reIssue");
        String reIssueTokenValue = memberService.reIssueRefreshToken(refreshToken);
        if (reIssueTokenValue == null) {
            return new ResponseEntity("기한만료!! 다시 로그인 해주세요",HttpStatus.FORBIDDEN);
        }
        TokenDto reIssueToken = TokenDto.builder()
                .token(reIssueTokenValue)
                .build();
        return new ResponseEntity(reIssueToken, HttpStatus.OK);
    }

    @GetMapping("/token-expiration-check/{accessToken}")
    public ResponseEntity tokenCheck(@PathVariable String accessToken){
        log.info("엑세스 토큰 유효기간 확인 요청");
        boolean result = jwtTokenProvider.validateToken(accessToken);
        //토큰의 유효기간 체크후 기한 남았으면 true / 기한 만료 되었으면 false
        System.out.println(result);
        if (result) //기한 유효시
            return new ResponseEntity(HttpStatus.OK);
        else //기한 만료시
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
    }

}
