package com.minair.minair.domain.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@NoArgsConstructor
public class LoginServiceDto {

    private boolean idNotMatch;
    private boolean wrongPwd;
    private boolean passLogin;
}
