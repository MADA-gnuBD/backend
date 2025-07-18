package com.MADA.mada_SeoulBike.domain.user.presentation;


import com.MADA.mada_SeoulBike.domain.user.domain.UserRepository;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserLoginRequest;
import com.MADA.mada_SeoulBike.domain.user.dto.request.UserRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.MediaType;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import com.jayway.jsonpath.JsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureMockMvc
class UserAuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private final String userId = "testuser123";
    private final String password = "TestPass123!";
    private final String nickname = "테스트닉네임";
    private final String email = "test@example.com";
    private final String userName = "홍길동";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() throws Exception {
        UserRequestDto signupDto = UserRequestDto.builder()
                .userId(userId)
                .password(password)
                .email(email)
                .nickname(nickname)
                .userName(userName)
                .build();

        mockMvc.perform(post("/users/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isCreated());

        assertThat(userRepository.findByUserId(userId)).isPresent();
    }

    @Test
    @DisplayName("회원가입 후 로그인 성공 및 토큰 반환")
    void login_after_signup_success() throws Exception {
        // 회원가입 먼저 진행
        signup_success();

        // 로그인 요청
        var loginDto = new UserLoginRequest(userId, password);

        MvcResult result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("accessToken");
        assertThat(response).contains("refreshToken");
    }
}
