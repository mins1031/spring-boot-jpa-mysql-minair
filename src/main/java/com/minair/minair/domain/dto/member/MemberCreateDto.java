package com.minair.minair.domain.dto.member;

import com.minair.minair.domain.notEntity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberCreateDto {

    private String username;
    private String password;
    private String email;
    private LocalDate birth;
    private String nameKor;
    private String nameEng;
    private String phone;
    private Gender gender;
}
