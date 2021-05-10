package com.minair.minair.jwt;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.time.LocalDateTime;
import java.util.UUID;

@Embeddable
@Getter
//@Builder
@NoArgsConstructor
public class RefreshTokenProperty {

    private String refreshTokenValue ;

    private long refreshTokenExpirationPeriod;

    public RefreshTokenProperty(String refreshToken,
                                long refreshTokenExpirationPeriod) {
        this.refreshTokenValue = refreshToken;
        this.refreshTokenExpirationPeriod = refreshTokenExpirationPeriod;
    }

    public void logout(){
        this.refreshTokenValue = null;
        this.refreshTokenExpirationPeriod = 0;
    }
    /*public static RefreshTokenProperty issueToken(){
        return RefreshTokenProperty.builder()
                .refreshToken(UUID.randomUUID().toString())
                .refreshTokenExpirationPeriod(14 * 24 * 60 * 60 * 1000L)//2week
                .build();
    }*/


}
