package com.minair.minair.jwt;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenProperty {

    private String secretKey = "min6038";
    private long tokenValidTime = 30* 60 * 1000L; // 30분
    private String headerValue = "Authorization";

}
