package com.minair.minair.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotNull(message = "로그인 시점에 필요한 이름을 입력해 주세요.")
    private String username;
    @NotNull(message = "로그인 시점에 필요한 비밀번호를 입력해 주세요.")
    private String password;
}
