package com.app.backend.member.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.app.backend.auth.application.ChangePasswordService;
import com.app.backend.auth.infrastructure.security.AuthMember;
import com.app.backend.auth.infrastructure.security.JwtAccessDeniedHandler;
import com.app.backend.auth.infrastructure.security.JwtAuthenticationEntryPoint;
import com.app.backend.auth.infrastructure.security.JwtAuthenticationFilter;
import com.app.backend.member.application.MemberProfileService;
import com.app.backend.member.application.result.MemberProfileResult;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restdocs.test.autoconfigure.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class MemberControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberProfileService memberProfileService;

    @MockitoBean
    private ChangePasswordService changePasswordService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("내 프로필 조회 API 문서 생성")
    void getMyProfile() throws Exception {
        AuthMember authMember = new AuthMember(1L, "ROLE_USER");
        setAuthentication(authMember);

        given(memberProfileService.getProfile(1L))
                .willReturn(new MemberProfileResult(
                        1L,
                        "test@example.com",
                        "테스트유저",
                        "010-1234-5678",
                        "ROLE_USER"
                ));

        mockMvc.perform(get("/api/v1/members/me"))
                .andExpect(status().isOk())
                .andDo(document("member/get-my-profile",
                        preprocessResponse(prettyPrint()),
                        relaxedResponseFields(memberProfileResponseFields())
                ));
    }

    @Test
    @DisplayName("내 프로필 수정 API 문서 생성")
    void updateProfile() throws Exception {
        AuthMember authMember = new AuthMember(1L, "ROLE_USER");
        setAuthentication(authMember);

        doNothing().when(memberProfileService)
                .updateProfile(eq(1L), eq("변경닉네임"));

        mockMvc.perform(patch("/api/v1/members/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "변경닉네임"
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("member/update-my-profile",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickname").description("변경할 닉네임")
                        ),
                        relaxedResponseFields(commonResponseFields())
                ));
    }

    @Test
    @DisplayName("비밀번호 변경 API 문서 생성")
    void changePassword() throws Exception {
        AuthMember authMember = new AuthMember(1L, "ROLE_USER");
        setAuthentication(authMember);

        doNothing().when(changePasswordService)
                .changePassword(any());

        mockMvc.perform(patch("/api/v1/members/me/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "currentPassword": "oldPassword123!",
                                  "newPassword": "newPassword123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andDo(document("member/change-password",
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("currentPassword").description("현재 비밀번호"),
                                fieldWithPath("newPassword").description("새 비밀번호")
                        ),
                        relaxedResponseFields(commonResponseFields())
                ));
    }

    private FieldDescriptor[] commonResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("success").description("요청 성공 여부"),
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("timestamp").description("응답 시간")
        };
    }

    private FieldDescriptor[] memberProfileResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("success").description("요청 성공 여부"),
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("message").description("응답 메시지"),
                fieldWithPath("data.memberId").description("회원 ID"),
                fieldWithPath("data.email").description("회원 이메일"),
                fieldWithPath("data.nickname").description("회원 닉네임"),
                fieldWithPath("data.phoneNumber").description("회원 전화번호"),
                fieldWithPath("data.role").description("회원 권한"),
                fieldWithPath("timestamp").description("응답 시간")
        };
    }

    private void setAuthentication(AuthMember authMember) {
        SecurityContextHolder.getContext().setAuthentication(authenticationToken(authMember));
    }

    private UsernamePasswordAuthenticationToken authenticationToken(AuthMember authMember) {
        return new UsernamePasswordAuthenticationToken(
                authMember,
                null,
                List.of(new SimpleGrantedAuthority(authMember.role()))
        );
    }
}
