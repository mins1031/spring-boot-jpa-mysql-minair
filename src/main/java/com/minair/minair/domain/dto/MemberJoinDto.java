package com.minair.minair.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.minair.minair.domain.notEntity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinDto {

    @NotNull(message = "ID는 꼭 입력해주세요.")
    private String username;
    @NotNull(message = "패스워드는 꼭 입력해주세요.")
    private String password;
    @NotNull(message = "Email은 꼭 입력해주세요.")
    private String email;
    @NotNull
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    @NotNull(message = "한글이름은 꼭 입력해주세요.")
    private String name_kor;
    @NotNull(message = "영문이름 꼭 입력해주세요.")
    private String name_eng;
    @NotNull(message = "폰 번호는 꼭 입력해주세요.")
    private String phone;
    @NotNull(message = "성별은 꼭 입력해주세요.")
    private Gender gender;

}
