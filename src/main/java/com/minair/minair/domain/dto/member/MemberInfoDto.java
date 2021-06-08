package com.minair.minair.domain.dto.member;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.notEntity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberInfoDto {

    private String username;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private String name_kor;
    private String name_eng;
    private String phone;
    private Gender gender;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime regDate;

    public static MemberInfoDto memberInfoDto(Member member){
        return MemberInfoDto.builder()
                .username(member.getUsername())
                .email(member.getEmail())
                .birth(member.getBirth())
                .name_kor(member.getName_kor())
                .name_eng(member.getName_eng())
                .phone(member.getPhone())
                .gender(member.getGender())
                .regDate(member.getRegDate())
                .build();
    }

}
