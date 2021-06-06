package com.minair.minair.api;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.minair.minair.domain.Member;
import com.minair.minair.domain.dto.LoginRequestDto;
import com.minair.minair.domain.dto.member.MemberJoinDto;
import com.minair.minair.domain.notEntity.Gender;
import com.minair.minair.jwt.RefreshTokenProperty;
import com.minair.minair.repository.MemberRepository;
import com.minair.minair.testconfig.RestDocsConfiguration;
import org.attoparser.IDocumentHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
public class MemberApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MemberRepository memberRepository;
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
        member.investRole("ROLE_MEMBER,ROLE_ADMIN");
        member2.investRole("ROLE_MEMBER");
        RefreshTokenProperty r = new RefreshTokenProperty();
        member.issueRefreshToken(r);
        member2.issueRefreshToken(r);

        memberRepository.save(member);
    }

    @Test
    public void join() throws Exception {
        //Given
        String username = "member";
        String password = "alsdud";
        String email = "mem@mem";
        String name_kor = "멤버";
        String name_eng = "member";
        String phone = "010-1111-0000";
        Gender gender = Gender.M;
        LocalDate birth = LocalDate.of(2020,8,19);
        MemberJoinDto memberJoinDto =
                new MemberJoinDto(username,password,email,birth,name_kor,name_eng,phone,gender);
        //When
        this.mockMvc.perform(post("/api/member/new")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(memberJoinDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").exists())
                .andExpect(jsonPath("password").doesNotExist())
                .andExpect(jsonPath("email").exists())
                .andExpect(jsonPath("birth").exists())
                .andExpect(jsonPath("name_kor").exists())
                .andExpect(jsonPath("name_eng").exists())
                .andExpect(jsonPath("phone").exists())
                .andExpect(jsonPath("gender").exists())
                .andExpect(jsonPath("_links.self.href").exists())
                .andExpect(jsonPath("_links.index.href").exists())
                .andExpect(jsonPath("_links.profile.href").exists())
        .andDo(document("join",
                links(
                        linkWithRel("self").description("self href"),
                        linkWithRel("index").description("index href"),
                        linkWithRel("profile").description("document href for explain this api")
                ),
                requestFields(
                        fieldWithPath("username").description("회원가입 유저 id값"),
                        fieldWithPath("password").description("회원가입 유저 비밀번호값"),
                        fieldWithPath("email").description("회원가입 유저 이메일 값"),
                        fieldWithPath("birth").description("회원가입 유저 생년월일 값"),
                        fieldWithPath("name_kor").description("회원가입 유저 이름 값"),
                        fieldWithPath("name_eng").description("회원가입 유저 영어이름 값"),
                        fieldWithPath("phone").description("회원가입 유저 핸드폰 번호값"),
                        fieldWithPath("gender").description("회원가입 유저 성별값")
                ),
                responseFields(
                        fieldWithPath("username").description("회원가입 유저 id 값"),
                        fieldWithPath("email").description("회원가입 유저 이메일 값"),
                        fieldWithPath("birth").description("회원가입 유저 생년월일 값"),
                        fieldWithPath("name_kor").description("회원가입 유저 이름 값"),
                        fieldWithPath("name_eng").description("회원가입 유저 영어이름 값"),
                        fieldWithPath("phone").description("회원가입 유저 핸드폰 번호 값"),
                        fieldWithPath("gender").description("회원가입 유저 성별 값"),
                        fieldWithPath("regDate").description("회원가입 유저 가입일시 값"),
                        fieldWithPath("_links.self.href").description("self href"),
                        fieldWithPath("_links.index.href").description("index href"),
                        fieldWithPath("_links.profile.href").description("document href for explain this api")
                )
        ))
        ;
    }

    @Test
    public void checkId() throws Exception {
        String id = "user1";
        String newId = "mmm";

        this.mockMvc.perform(get("/api/member/checkId/{id}",newId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("YES"))
        ;
    }

    @Test
    public void login() throws Exception {
        String username = "user1";
        String password = "alsdud";

        LoginRequestDto loginRequestDto = new LoginRequestDto(username,password);
        this.mockMvc.perform(post("/api/member/login")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").exists())
        .andDo(document("login",
                requestFields(
                        fieldWithPath("username").description("사용자 id 값"),
                        fieldWithPath("password").description("사용자 password 값")
                ),
                responseFields(
                        fieldWithPath("token").description("엑세스 토큰 값")
                )
                ))
        ;
    }

    @Test
    public void logout() {
    }

    @Test
    public void checkAdmin() {
    }
}