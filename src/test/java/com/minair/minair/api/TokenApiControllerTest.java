package com.minair.minair.api;

import com.minair.minair.domain.Member;
import com.minair.minair.domain.MemberRole;
import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.JwtTokenProvider;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.service.MemberService;
import com.minair.minair.testconfig.RestDocsConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@Import(RestDocsConfiguration.class)
public class TokenApiControllerTest {

    @Autowired
    MemberService memberService;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Before
    public void before(){

        Member member = Member.joinMember("user1",passwordEncoder.encode("alsdud"),"min@min",
                LocalDate.of(2021,05,30),"민","min","010-2222-2222",
                Gender.F);
        Member member2 = Member.joinMember("user2",passwordEncoder.encode("jae"),"jae@jae",
                LocalDate.of(2021,05,30),"재","jae","010-2222-2222",
                Gender.M);
        MemberRole memberRole = MemberRole.ROLE_ADMIN;
        member.investMemberRole(memberRole);
        MemberRole memberRole2 = MemberRole.ROLE_MEMBER;
        member2.investMemberRole(memberRole2);
        RefreshTokenProperty r = new RefreshTokenProperty(
                UUID.randomUUID().toString(), new Date().getTime()
        );

        member.issueRefreshToken(r);
        member2.issueRefreshToken(r);

        memberRepository.save(member);
    }

    public void generateMember(int count){
        for (int i = 0; i<count;i++) {
            MemberJoinDto member = new MemberJoinDto("testId"+i,"testPw"+i,
                    "testEmail"+i, LocalDate.of(2021,06,07),
                    "testNameKor"+i, "testNameEng"+i,
                    "testPhone"+i, Gender.M);

            memberService.join(member);
        }
    }

    @Test
    public void refresh() throws Exception {
        generateMember(15);
        String username = "testId2";

        this.mockMvc.perform(post("/api/token/refresh/{username}",username))
                .andDo(print())
                .andExpect(status().isOk())
        .andExpect(jsonPath("token").exists())
        .andDo(document("refreshToken-issue",
                responseFields(
                        fieldWithPath("token").description("발급 된 refreshToken 값")
                )
                ))
                ;
    }

    @Test
    public void reIssue() throws Exception{
        generateMember(3);
        String username = "user1" ;
        Member member = memberRepository.findByUsername(username);
        String refreshTokenValue =
                jwtTokenProvider.createRefreshToken(member.getRefreshToken());

        this.mockMvc.perform(post("/api/token/reissue/{refreshToken}",refreshTokenValue))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void tokenCheck() throws Exception {
        String username = "user1";
        Member byUsername = memberRepository.findByUsername(username);

        String token = jwtTokenProvider.createToken(byUsername.getUsername(),byUsername.getRole());

        this.mockMvc.perform(get("/api/token/tokenExpirationCheck/{accessToken}",token))
                .andDo(print())
                .andExpect(status().isOk())
                ;
    }
}