package com.minair.minair.domain.dto;

import com.minair.minair.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class MemberListDto {

    private Long id;
    private String username;
    private String email;
    private String name_kor;
    private String phone;

    public static MemberListDto memberListDto(Member member){
        return MemberListDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .email(member.getEmail())
                .name_kor(member.getName_kor())
                .phone(member.getPhone())
                .build();
    }
}
