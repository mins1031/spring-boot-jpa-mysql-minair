package com.minair.minair.service;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.MemberJoinDto;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.StackSize;
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
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void join(MemberJoinDto memberJoinDto){
        String encodePw = passwordEncoder.encode(memberJoinDto.getPassword());
        String role = "ROLE_MEMBER";
        Member joinMember = Member.joinMember(memberJoinDto.getUsername(),
                        encodePw, memberJoinDto.getEmail(),
                        memberJoinDto.getBirth(),memberJoinDto.getName_kor(),
                        memberJoinDto.getName_eng(),memberJoinDto.getPhone(),
                        memberJoinDto.getGender());
        joinMember.investRole(role);

        RefreshTokenProperty refreshTokenProperty
                = new RefreshTokenProperty(null,0);
        joinMember.issueRefreshToken(refreshTokenProperty);

        memberRepository.save(joinMember);
    }

    @Transactional
    public void joinAdmin(MemberJoinDto memberJoinDto){
        String encodePw = passwordEncoder.encode(memberJoinDto.getPassword());
        String role = "ROLE_MEMBER,ROLE_ADMIN";
        Member joinMember = Member.joinMember(memberJoinDto.getUsername(),
                encodePw, memberJoinDto.getEmail(),
                memberJoinDto.getBirth(),memberJoinDto.getName_kor(),
                memberJoinDto.getName_eng(),memberJoinDto.getPhone(),
                memberJoinDto.getGender());
        joinMember.investRole(role);

        RefreshTokenProperty refreshTokenProperty
                = new RefreshTokenProperty(null,0);
        joinMember.issueRefreshToken(refreshTokenProperty);

        memberRepository.save(joinMember);
    }

    public Optional<Member> findById(Long id){
        return memberRepository.findById(id);
    }

    public List<Member> findByAll(){
        return memberRepository.findAll();
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

    @Transactional
    public boolean logout(String refreshToken){
        String convertToken = jwtTokenProvider.getMemberName(refreshToken);
        Member member = memberRepository.findByRefreshToken(convertToken);
        log.info("로그아웃 서비스");
        boolean logoutCheck = false;
        if (member != null) {
            logoutCheck = true;
            member.logout();
        }

        return logoutCheck;
    }

    @Transactional
    public Member findByUserInfo(String username){
        Member findMember = memberRepository.findByUsername(username);
        if (findMember == null)
            new NullPointerException();

        return findMember;

    }
}
