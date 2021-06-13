package com.minair.minair.service;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.Reservation;
import com.minair.minair.domain.dto.member.*;
import com.minair.minair.domain.dto.token.TokenDto;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ModelMapper modelMapper;

    @Transactional
    public MemberInfoDto join(MemberJoinDto memberJoinDto){
        String encodePw = passwordEncoder.encode(memberJoinDto.getPassword());
        MemberRole memberRole = MemberRole.ROLE_MEMBER;
        Member joinMember = Member.joinMember(memberJoinDto.getUsername(),
                        encodePw, memberJoinDto.getEmail(),
                        memberJoinDto.getBirth(),memberJoinDto.getNameKor(),
                        memberJoinDto.getNameEng(),memberJoinDto.getPhone(),
                        memberJoinDto.getGender());
        joinMember.investMemberRole(memberRole);

        RefreshTokenProperty refreshTokenProperty
                = new RefreshTokenProperty(null,0);
        joinMember.issueRefreshToken(refreshTokenProperty);

        Member save = memberRepository.save(joinMember);
        MemberInfoDto resultMember = modelMapper.map(save,MemberInfoDto.class);

        return resultMember;
    }

    //@Transactional
    public boolean login(LoginRequestDto loginRequestDto) {
        Member member = memberRepository.findByUsername(loginRequestDto.getUsername());
        boolean result;

        if (member == null) {
            result = false;
            return result;
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), member.getPassword())) {
            log.info("not match password!");
            result = false;
        } else {
            log.info("pw clean!");
            result = true;
        }
        return result;
    }

    @Transactional
    public void joinAdmin(MemberJoinDto memberJoinDto){
        String encodePw = passwordEncoder.encode(memberJoinDto.getPassword());
        MemberRole memberRole = MemberRole.ROLE_ADMIN;
        Member joinMember = Member.joinMember(memberJoinDto.getUsername(),
                encodePw, memberJoinDto.getEmail(),
                memberJoinDto.getBirth(),memberJoinDto.getNameKor(),
                memberJoinDto.getNameEng(),memberJoinDto.getPhone(),
                memberJoinDto.getGender());
        joinMember.investMemberRole(memberRole);

        RefreshTokenProperty refreshTokenProperty
                = new RefreshTokenProperty(null,0);
        joinMember.issueRefreshToken(refreshTokenProperty);

        memberRepository.save(joinMember);
    }

    public Optional<Member> findById(Long id){
        return memberRepository.findById(id);
    }

    public Page<Member> findByAll(int pageNum){

        int offset = pageNum - 1;
        PageRequest pageRequest = PageRequest.of(offset,10);

        Page<Member> members = memberRepository.findMembers(pageRequest);
        if (members.getContent().isEmpty())
            throw new NullPointerException();

        return members;
    }

    public void updateMember(Member member){
        Long findId = member.getId();
        Optional<Member> optionalMember = memberRepository.findById(findId);
        Member findMember = optionalMember.get();

    }

    @Transactional
    public void issueRefreshToken(String username, RefreshTokenProperty r){
        try {
            Member member = memberRepository.findByUsername(username);
            member.issueRefreshToken(r);
        }catch (NullPointerException e){

        }
    }//refreshToken DB에 저장

    @Transactional
    public Member findUserInfo(String token){
        String username = jwtTokenProvider.getMemberName(token);
        Member findMember = memberRepository.findByUsername(username);
        return findMember;
    }

    @Transactional
    public Member reIssueRefreshToken(String refreshToken){
        log.info("service: reIssueRefreshToken");
        Member memberByRefreshToken = null;
        if (jwtTokenProvider.validateRefreshToken(refreshToken)){
            System.out.println("리프레시 토큰 기한 유효!!");
            String realValue = jwtTokenProvider.getMemberName(refreshToken);
            System.out.println("복호화 토큰="+realValue);
            memberByRefreshToken = memberRepository.findByRefreshToken(realValue);
        }
        return memberByRefreshToken;
    }

/**
 * 과연 리턴 값을 boolean으로? 아님 따로 클래스 만들어서...?
 * 불린으로 하면 컨트롤러에서 토큰 값을 생성해줘야함. 따로 DTO만들어서 리턴하는것도 좋아보임
 * 범용성 있게..는 불가하고 LoginResultDto 만들어서 ture,false값과 true면 member 변환해서 보내줌.
 * => 모든 디비와의 동작은 서비스까지만 오게 구현 해볼것.(정말 간단한 동작은 바로 오고가는것 가능.)
 */

    @Transactional
    public boolean logout(String username){
        log.info("로그아웃 서비스");
        Optional<Member> optionalMember = Optional.ofNullable(memberRepository.findByUsername(username));
        Member member = optionalMember.get();

        boolean logoutResult = false;
        if (member != null) {
            logoutResult = true;
            member.logout();
        }
        return logoutResult;
    }

    @Transactional
    public Member findByUserInfo(String username){
        Member findMember = memberRepository.findByUsername(username);
        if (findMember == null)
            new NullPointerException();

        return findMember;
    }

    @Transactional
    public void updateMember(MemberModifyDto memberModifyDto){
        Member findMember = memberRepository.findByUsername(memberModifyDto.getUsername());
        findMember.updateMember(memberModifyDto);
    }

    @Transactional
    public boolean delete(String username) {

        boolean reservationDateCheck = reservationRepository.findReservationNotOverDate(username);
        boolean result = true;

        if (!reservationDateCheck){ //reservationDateCheck = false => 현재날짜 이후의 예약이 없는 경우 바로 삭제
            Member member = memberRepository.findByUsername(username);
            memberRepository.delete(member);
            result = false;
        }
        return result;
    }
    //폴인키 묶인것 무시하고 그냥 삭제하려 했는데 안됨. =>  결국 모든 예약 값들도 같이 지워줘야함.
    //이건 기준정해서 예약 값남기고 예약자만 null처리 할건지, 아예 모두 삭제 시켜버릴건지 정하고 구현할것.
}
