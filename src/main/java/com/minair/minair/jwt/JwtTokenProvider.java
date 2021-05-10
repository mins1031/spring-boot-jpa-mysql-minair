package com.minair.minair.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.minair.minair.auth.PrincipalDetails;
import com.minair.minair.auth.PrincipalDetailsService;
import com.minair.minair.domain.Member;
import com.minair.minair.repository.MemberRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenProvider {

    AccessTokenProperty access = new AccessTokenProperty();

    private String secretKey = access.getSecretKey();

    private final PrincipalDetailsService principalDetailsService;
    private final MemberRepository memberRepository;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        //서버 고유값 Base64로 암호화
    }

    //Jwt토큰 생성
    public String createToken(String userPk, List<String> roles){
        Claims claims = Jwts.claims().setSubject(userPk);
        //payload에 저장되는 정보
        claims.put("roles",roles);// 정보는 key/value로 저장됨
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)//정보 저장
                .setIssuedAt(now)//토큰 발행시간 정보
                .setExpiration(new Date(now.getTime()+/*access.getTokenValidTime()*/
                60*1*1000L))
                //토큰 만료시간 설정.현재시간 + 30분
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }

    //refreshToken 생성
    public String createRefreshToken(RefreshTokenProperty r){
        Claims claims = Jwts.claims().setSubject(r.getRefreshTokenValue());
        claims.put("value",r.getRefreshTokenValue());
        Date date = new Date();
        Date expiration =
                new Date(date.getTime() + r.getRefreshTokenExpirationPeriod());
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }
    //JWT 토큰에서 인증정보 조회
    public Authentication getAuthentication(String token){
        log.info("getAuthentication");
        PrincipalDetails principalDetails =
                (PrincipalDetails) principalDetailsService.loadUserByUsername(getMemberName(token));

        return new UsernamePasswordAuthenticationToken(principalDetails,"", principalDetails.getAuthorities());
    }

    //토큰에서 회원정보 추출 메서드
    public String getMemberName(String token){
        log.info("getMemberName");
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    //Request의 header에서 token값을 가져온다
    public String resolveToken(HttpServletRequest request){
        log.info("resolveToken");
        return request.getHeader(access.getHeaderValue());
    }

    //Request의 header에서 refreshToken값을 가져온다
    public String resolveRefreshToken(HttpServletRequest request){
        log.info("refreshToken");
        return request.getHeader("refreshToken");
    }

    //토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken){
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
            //만료일자가 넘지않으면 true인듯
        }catch (Exception e){
            return false;
        }
    }
    //try안에서 if - else로 나누고 else면 밑의 메서드 실행해 refresh토큰값 확인하고
    //이 값의 만료일자를 확인하고 만료일자가 안지났다면 토큰값 db에서 확인하고
    //맞으면 새 access토큰 응답하는 방식으로 활용

    public boolean validateRefreshToken(String refreshToken){
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(refreshToken);
            return !claims.getBody().getExpiration().before(new Date());
            //조금 헷갈리는데 만료기한이 현시간보다 전 = 만료 (true), 현시간보다 후 = 만료안됨(false) 이기에
            //not연산(!)을 해주면 -> 만료안됨 = true출력, 만료 = false 출력
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
}
