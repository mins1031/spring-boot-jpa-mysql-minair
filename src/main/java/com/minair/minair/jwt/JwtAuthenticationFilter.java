package com.minair.minair.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.minair.minair.auth.PrincipalDetails;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.LoginRequestDto;
import com.minair.minair.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

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
        String token = jwtTokenProvider.resolveToken((HttpServletRequest) request);
        String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);

        //유효한 토큰인지 확인
        if (token != null && jwtTokenProvider.validateToken(token)){
            System.out.println("토큰이 유효함");
            //토큰이 유효하면 토큰으로 부터 유저 정보를 받아옴
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            //securityContext에 Authentication객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else{
            System.out.println("토큰이 유효하지 않음");
            /*if (token != null && refreshToken != null) {
                String result = jwtTokenProvider.validateRefreshToken(refreshToken, username);
                //새 엑세스 토큰값 출력
                if (result != null) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(result);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("result=" + result);
                    response.addHeader("Authorization",result);
                }
            }*/
        }
        //검증시 필요한 값은 새 access토큰(id, roles),
        // 회원id와 동일한 값을 db에서 가져와 헤더의 refreshToken값과 비교
        //비교후 둘이 동일하다면 새 토큰 응답해줘야함.

        //else if의 조건이 true라면 refresh 토큰의 기한이 살아있기때문에
        //기존 죽은Access토큰의 id값을 가져와 member를 가져오고 Role을 받아와 토큰을 새로 생성해 응답해준다.
        filterChain.doFilter(request,response);
    }


  /*  refreshToken != null && jwtTokenProvider.validateRefreshToken(refreshToken)
            System.out.println("refresh토큰이 유효함");
    String userId = jwtTokenProvider.getMemberName(token);
    Member member = memberRepository.findByUsername(userId);
            jwtTokenProvider.createToken(userId,member.getRoleList());
            System.out.println(jwtTokenProvider.createToken(userId,member.getRoleList()));
    String reToken = jwtTokenProvider.createToken(userId,member.getRoleList());
*/



    /*private final AuthenticationManager authenticationManager;

    // /login요청을 하면 로그인 시도를 위해 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter로 로그인 시도");

        ObjectMapper om;
        LoginRequestDto member;
        try {
            om = new ObjectMapper();//json파싱 클래스;
            member = om.readValue(request.getInputStream(), LoginRequestDto.class);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUsername(),
                            member.getPassword());
            //UsernamePasswordAuthenticationToken토큰 id,pw넣어 직접생성

            Authentication authentication =
                    authenticationManager.authenticate(authenticationToken);
            //위의 코드가 실행될때 PrincipalDetailsService의 loadUserByUsername 메서드가 실행행
            //authenticationManager에 토큰을 넣어서 던지면 인증해서 다시 반환해줌
            //그래서 반환값은 authentication엔 로그인한 정보가 담김
            //DB에 있는 username과 password가 일치한다면 위의 코드가 동작함.
            //authentication은 권한문제로 시큐리티세션영역에 저장해줌.

            //밑의 getPrincipal()이 잘 나온다면 세션에 잘 저장되었다는뜻 = 로그인되었다는 의미
            PrincipalDetails principalDetails =
                    (PrincipalDetails) authentication.getPrincipal();
            System.out.println("로그인완료:" + principalDetails.getMember().getUsername());
            return authentication;
        } catch (MismatchedInputException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    /***
     * attemptAuthentication(위 메서드)실행후 인증이 정상적으로 되었으면
     * 밑의 메서드가 실행됨. 여기서 Jwt토큰 생성해 요청한 클라이언트에 토큰을 넣어 응답해줌.
     */
    /*@Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        System.out.println("인증 완료");
        PrincipalDetails principalDetails =
                (PrincipalDetails) authResult.getPrincipal();
        System.out.println("principal=" + principalDetails.getUsername());

        String jwtToken = JWT.create()
                .withSubject("minairToken")
                .withExpiresAt(new Date(System.currentTimeMillis() + (60000 * 10)))
                .withClaim("pk", principalDetails.getMember().getId())
                .withClaim("id", principalDetails.getUsername())
                .sign(Algorithm.HMAC256("min6038"));
        System.out.println(jwtToken);
        //Cookie cookie = new Cookie("Authorization","Bearer "+jwtToken);
        response.addHeader("Authorization", "Bearer " + jwtToken);
        //response.addCookie(cookie);
    }*/
}
//JwtAuthenticationFilter에선 id,pw를 통해 로그인하고 정상이면 jwt토큰 생성해
// 클라쪽으로 응답하는 인증과정을 거침
//이젠 요청시마다 jwt토큰을 가지고 요청할 것이고 서버는 토큰이 유효한지를 판단해야하기에
//새로운 필터로 판단을 해줌.
