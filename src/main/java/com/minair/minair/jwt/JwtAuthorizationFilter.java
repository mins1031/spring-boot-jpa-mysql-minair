package com.minair.minair.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.minair.minair.auth.PrincipalDetails;
import com.minair.minair.domain.Member;
import com.minair.minair.repository.MemberRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * BasicAuthenticationFilter는 권한이나 인증이 필요한 특정 주소를 요청했을때 거치는
 * 필터임. 권한이나 인증이 필요한 url이 아니라면 이 필터를 타지 않음.
 * */

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  MemberRepository memberRepository) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String jwtHeader = request.getHeader("Authorization");
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")){
            //헤더가 없거나 Bearer로 시작하지 않는 경우
            chain.doFilter(request,response);
            return;
        } else {
            System.out.println("인증이나 권한이 필요한 주소가 요청됨");
            System.out.println("jwtHeader:"+jwtHeader);
            //우선 로그인해서 토큰값받고 헤더의 Authorization에 value로 넣고
            //jwt 토큰을 검증해서 정상적이 사용자인지 확인
        }

        String jwtToken = request.getHeader("Authorization")
                .replace("Bearer ","");
        //토큰값에서 Bearer 부분을 지우고 암호화한 값만 남김.

        String username = JWT.require(Algorithm.HMAC256("min6038"))
                  .build().verify(jwtToken).getClaim("id").asString();
        //토큰 클레임중 id클레임을 디비값과 비교하기 위해 암호화 해제하는 것.

        //username에 값이 있으면 서명이 잘된것임.
        if (username != null){
            Member member = memberRepository.findByUsername(username);

            PrincipalDetails principalDetails =
                    new PrincipalDetails(member);

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails,
                            null,principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request,response);
    }
}
