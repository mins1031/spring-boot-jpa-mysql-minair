package com.minair.minair.config;

import com.minair.minair.jwt.JwtAuthenticationFilter;
import com.minair.minair.jwt.JwtAuthorizationFilter;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
/*@Configuration과 @EnableWebSecurity같이 사용해 spring security 설정할 클래스
라는걸 명시해줌, 설정은 WebSecurityConfigurerAdapter 클래스를 상속받아 메서드를 구현
하는것이 일반적인 방법임. +스프링 시큐리티가 필터체인에 등록됨*/
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//WebSecurityConfigurerAdapter는 WebSecurityConfigurer 인스턴스를 편리하게 생성해줌

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    //스프링 시큐리티에서 제공하는 비밀번호 암호화 객체임. service단에서 암호화 할수
    //있도록 Bean으로 등록해줘야 한다고함.

  /*  @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**","/js/**",
                "/images/**","/lib/**","/font/**","/sass/**");
        //resource/static 디렉터리의 하위 파일목록은 항상통과(인증무시)
    }/*WebSecurity는 FilterChainProxy를 생성하는 필터임. 위의 로직코드는
     해당경로의 파일들은 스프링시큐리티가 무시하도록 설정.*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
                //.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                  //      UsernamePasswordAuthenticationFilter.class);
        http.authorizeRequests()
                //.antMatchers("/auth/**").access("hasRole('ROLE_MEMBER')")
                /*antMatchers("/admin/**")
                .access("hasRole('ROLE_ADMIN')")
                //ADMIN롤을 가진 사용자만 접근가능한 uri설정
                .antMatchers("/user/**")
                .access("hasRole('ROLE_MEMBER') or hasRole('ROLE_ADMIN')")
                //MEMBER롤을 가진 사용자만 접근가능한 uri설정*/
                .antMatchers("/**").permitAll()
                //권한 상관없이 접근 가능한 uri
                /*.anyRequest().authenticated() => 모든 요청에 대해 인증된
                * 사용자만 접근하도록 설정함. 아마 디폴트 인듯,..?*/
                //페이지 권한 설정
                /*.and()
                    .formLogin()
                        .loginPage("/user/login")
                        .defaultSuccessUrl("/user/login/result")
                        .permitAll()
                .and()   //로그인 설정
                    .logout()
                        .logoutRequestMatcher(
                                new AntPathRequestMatcher("/user/logout"))
                        .logoutSuccessUrl("/user/logout/result")
                        .invalidateHttpSession(true)*/
                .and()
                    //403 exception 핸들링
                    .exceptionHandling().accessDeniedPage("/user/denied");

    }
    /*HttpSecurity를 통해 http요청에 대한 웹기반 보안을 구성할수 있음 http를 객체로 만들어주는듯?
    * authorizeRequests() : HttpServletRequest에 따라 접근을 제한함
    *    antMatchers메서드로 특정 경로를 지정하며 permitALl(),hasRole등의 메서드로
    * 역할에따른 접근설정을 해줄수 있음. */


}
