package com.minair.minair.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    //CorsFilter : 스프링이 들고 있는 CorsFilter라고함
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        //서버가 응답을 할때 json을 자바스크립트에서 처리할수 있게 할지를 설정하는것
        //false로 되어있으면 자바스크립트로 오는 요청에 응답이 안됨
        config.addAllowedOrigin("*");//모든 ip에 응답을 허용하겠다
        config.addAllowedHeader("*");//모든 헤더에 응답을 허용하겠다
        config.addAllowedMethod("*");//모든 http 메서드 요청을 허용하겠다.
        source.registerCorsConfiguration("/**",config);
        //해당 uri를 따르는 모든 요청은 해당 config설정을 따르라는 명령
        return new CorsFilter(source);
    }//만들고 끝이 아니라 필터에 등록 해줘야함.
}
