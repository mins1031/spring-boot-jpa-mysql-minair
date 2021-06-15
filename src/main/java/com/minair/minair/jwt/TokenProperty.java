package com.minair.minair.jwt;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenProperty {

    private final String secretKey = "min6038";
    private final long tokenValidTime = 30* 60 * 1000L; // 30분
    private final String headerValue = "Authorization";
    private final long accessTokenValidTime = 1209600; // 2주

}
