package com.minair.minair.jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 기존엔 /login를 post로 id,pw를 담아 전송하면
 * UsernamePasswordAuthenticationFilter가 가로채 실행되는 원리.
 * 다만 지금은 /login은 막아놔서 실행x.그래서 강제로 동작시키기위해 UsernamePasswordAuthenticationFilter를
 * 상속받고 직접 구현해줌
 * */

@RequiredArgsConstructor/*GenericFilterBean*/
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //header에서 Jwt 받아옴
        String token = jwtTokenProvider.resolveToken(request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken(request);

        //유효한 토큰인지 확인
        if (token != null && jwtTokenProvider.validateToken(token)){
            System.out.println("토큰이 유효함");
            //토큰이 유효하면 토큰으로 부터 유저 정보를 받아옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            //securityContext에 Authentication객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else{
            System.out.println("토큰이 유효하지 않음");
        }
        filterChain.doFilter(request,response);
    }
}
//JwtAuthenticationFilter에선 id,pw를 통해 로그인하고 정상이면 jwt토큰 생성해
// 클라쪽으로 응답하는 인증과정을 거침
//이젠 요청시마다 jwt토큰을 가지고 요청할 것이고 서버는 토큰이 유효한지를 판단해야하기에
//새로운 필터로 판단을 해줌.
