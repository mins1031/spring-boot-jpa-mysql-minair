package com.minair.minair.domain;

import com.minair.minair.domain.date.DateEntity;
import com.minair.minair.domain.dto.member.MemberModifyDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
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

    //private String roles;

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

    @Builder
    public Member(Long id, String username, String password, String email, LocalDate birth,
                  String nameKor, String nameEng, String phone, Gender gender) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.birth = birth;
        this.nameKor = nameKor;
        this.nameEng = nameEng;
        this.phone = phone;
        this.gender = gender;
    }

    //Member생성 메서드
    public static Member joinMember(String username,String password,
                             String email, LocalDate birth,String nameKor,
                             String nameEng, String phone,
                             Gender gender){

        /*return Member.builder()
                .username(username)
                .password(password)
                .email(email)
                .birth(birth)
                .nameKor(nameKor)
                .nameEng(nameEng)
                .phone(phone)
                .gender(gender)
                .build();
*/

        Member member = new Member();
        member.username  = username;
        member.password = password;
        member.email = email;
        member.nameEng = nameEng;
        member.nameKor = nameKor;
        member.phone = phone;
        member.birth = birth;
        member.gender = gender;

        return member;
    }

    public void investMemberRole(MemberRole roles){
        this.roles = roles;
    }
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
