package com.minair.minair.domain.dto;

import com.minair.minair.domain.notEntity.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberJoinDto {

    @NotBlank(message = "ID는 꼭 입력해주세요.")
    private String username;
    @NotBlank(message = "패스워드는 꼭 입력해주세요.")
    private String password;
    @NotBlank(message = "Email은 꼭 입력해주세요.")
    private String email;
    @NotBlank(message = "생년월일은 꼭 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    @NotBlank(message = "한글이름은 꼭 입력해주세요.")
    private String name_kor;
    @NotBlank(message = "영문이름 꼭 입력해주세요.")
    private String name_eng;
    @NotBlank(message = "폰 번호는 꼭 입력해주세요.")
    private String phone;
    @NotBlank(message = "성별은 꼭 입력해주세요.")
    private Gender gender;

}
