package com.minair.minair.domain.dto.member;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.notEntity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberListDto {

    private Long id;
    private String username;
    private String email;
    private String name_kor;
    private String name_eng;
    private String phone;
    private Gender gender;
    private LocalDateTime regDate;

    public static MemberListDto memberListDto(Member member){
        return MemberListDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .name_kor(member.getNameKor())
                .phone(member.getPhone())
                .build();
    }
}
