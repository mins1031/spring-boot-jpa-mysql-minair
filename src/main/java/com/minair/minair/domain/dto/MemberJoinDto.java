package com.minair.minair.domain.dto;

import com.minair.minair.domain.notEntity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberJoinDto {

    private String username;
    private String password;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private String name_kor;
    private String name_eng;
    private String phone;
    private Gender gender;

}
