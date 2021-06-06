package com.minair.minair.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minair.minair.domain.date.DateEntity;
import com.minair.minair.domain.dto.member.MemberModifyDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import lombok.AccessLevel;
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

    private String name_kor;
    private String name_eng;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String roles;

    @Embedded
    @JsonIgnore
    private RefreshTokenProperty refreshToken;

    public List<String> getRoleList(){
        if(this.roles.length() > 0){
            return Arrays.asList(this.roles.split(","));
            //Arrays.asList는 배열을 리스트형태로 변환하지만 변환된 리스트가 배열의 주소값을 매게로 형태만 변하기에
            //Arrays.asList로만든 리스트는 값의 추가가 안되고 값을 변경하면 기존 배열의 값까지 변해버림.
            //이 경우는 값을 추가하지 못하게 하기 위함임.
        }
        return new ArrayList<>();
    }
    /*@OneToMany(mappedBy = "member" , cascade = CascadeType.ALL)
    private List<Reservation> reservationList = new ArrayList<>();
    회원에서 예약을 조회하는것보다 예약에서 회원값으로 조회하는게 더 객체지향적임.*/

    //Member생성 메서드
    public static Member joinMember(String username,String password,
                             String email, LocalDate birth,String name_kor,
                             String name_eng, String phone,
                             Gender gender){

        Member member = new Member();
        member.username  = username;
        member.password = password;
        member.email = email;
        member.name_eng = name_eng;
        member.name_kor = name_kor;
        member.phone = phone;
        member.birth = birth;
        member.gender = gender;

        return member;
    }

    public void investRole(String roles){
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
        this.name_kor = memberModifyDto.getName_kor();
        this.name_eng = memberModifyDto.getName_eng();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id) && Objects.equals(username, member.username) && Objects.equals(password, member.password) && Objects.equals(email, member.email) && Objects.equals(birth, member.birth) && Objects.equals(name_kor, member.name_kor) && Objects.equals(name_eng, member.name_eng) && Objects.equals(phone, member.phone) && gender == member.gender && Objects.equals(roles, member.roles) && Objects.equals(refreshToken, member.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, email, birth, name_kor, name_eng, phone, gender, roles, refreshToken);
    }


}
