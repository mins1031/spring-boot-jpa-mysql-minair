package com.minair.minair.domain;

import com.minair.minair.domain.date.DateEntity;
import com.minair.minair.domain.dto.member.MemberCreateDto;
import com.minair.minair.domain.dto.member.MemberModifyDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Member extends DateEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;
    private String password;
    private String email;

    private LocalDate birth;

    private String nameKor;
    private String nameEng;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "roles")
    private MemberRole roles;

    /**
     * enum으로 바꾼 시점에서 체크해야 할부분 : 권한 부여 부분, 체크 부분, 디비 권한값 하나로 바꾸기 getRoleList사용하는곳
     * 모두 단일 Role체크로 바꾸기.
     */
    @Embedded
    private RefreshTokenProperty refreshToken;

    public MemberRole getRole(){
        return this.roles;
    }

    /*@OneToMany(mappedBy = "member" , cascade = CascadeType.ALL)
    private List<Reservation> reservationList = new ArrayList<>();
    회원에서 예약을 조회하는것보다 예약에서 회원값으로 조회하는게 더 객체지향적임.*/

    //Member생성 메서드
    public static Member createMember(MemberCreateDto member,MemberRole memberRole) {

        return Member.builder()
                .username(member.getUsername())
                .password(member.getPassword())
                .email(member.getEmail())
                .birth(member.getBirth())
                .nameKor(member.getNameKor())
                .nameEng(member.getNameEng())
                .phone(member.getPhone())
                .gender(member.getGender())
                .roles(memberRole)
                .build();
    }

    /**
     * 1. 반복되는 코드 줄임
     * 2. for문 지양
     * 3. 각 역할에 맞는 로직인지 판단
     *    -> 컨트롤러: 최대한 값이 오고 가는 것들만 구성하는게 좋다
     *    -> 서비스: 최대한 '로직'자체를 구성하려고 노력하며 컨트롤러에 결과 전달시 OSIV를 고려해 DTO를 리턴할것.
     *
     * */

    public void issueRefreshToken(RefreshTokenProperty refreshTokenProperty){
        this.refreshToken = refreshTokenProperty;
        log.info(refreshToken.getRefreshTokenValue());
        System.out.println("Expiration="+
                refreshToken.getRefreshTokenExpirationPeriod());
    }

    public void updateMember(MemberModifyDto memberModifyDto){
        this.email = memberModifyDto.getEmail();
        this.nameKor = memberModifyDto.getName_kor();
        this.nameEng = memberModifyDto.getName_eng();
        this.phone = memberModifyDto.getPhone();
        this.birth = memberModifyDto.getBirth();
        this.gender = memberModifyDto.getGender();
    }

    public void changePassword(String password){
        this.password = password;
    }

    public void logout(){
        this.refreshToken.logout();
    }

}
