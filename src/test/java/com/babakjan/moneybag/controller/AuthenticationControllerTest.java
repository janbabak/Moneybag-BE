package com.babakjan.moneybag.controller;

import com.babakjan.moneybag.dto.user.AuthenticationRequest;
import com.babakjan.moneybag.dto.user.AuthenticationResponse;
import com.babakjan.moneybag.dto.user.RegisterRequest;
import com.babakjan.moneybag.dto.user.UserDto;
import com.babakjan.moneybag.entity.Role;
import com.babakjan.moneybag.service.AuthenticationService;
import com.babakjan.moneybag.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@Import(JwtService.class)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthenticationService authenticationService;
    private final String ROOT_URL = "/auth";

    @Test
    @WithMockUser
    void authenticate() throws Exception {
        // given
        AuthenticationRequest request = new AuthenticationRequest(
                "john.doe@gmail.com",
                "40580jkdjfJIJj"
        );
        String requestContent = """
                {
                    "email": "john.doe@gmail.com",
                    "password": "40580jkdjfJIJj"
                }""";
        AuthenticationResponse response = new AuthenticationResponse(
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJleHAiOjE2ODU4NzQ3MzksImlhdCI6MTY4NTc4ODMzOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dfQ.G4axFV8xRkzcpLulzPXp-bRuRc3lQIs2vp_jkb6LxLw",
                new UserDto(
                        454L,
                        "John",
                        "Doe",
                        "john.doe@gmail.com",
                        Role.USER,
                        "EUR",
                        new ArrayList<>()
                )
        );
        String responseContent = """

                {
                    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJleHAiOjE2ODU4NzQ3MzksImlhdCI6MTY4NTc4ODMzOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dfQ.G4axFV8xRkzcpLulzPXp-bRuRc3lQIs2vp_jkb6LxLw",
                    "user": {
                        "id": 454,
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@gmail.com",
                        "role": "USER",
                        "currency": "EUR",
                        "accountIds": []
                    }
                }""";
        given(authenticationService.authenticate(request))
                .willReturn(response);

        // then
        mockMvc
                .perform(
                        post(ROOT_URL + "/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestContent)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(responseContent));
    }

    @Test
    @WithMockUser // FIXME: for some reason doesn't work without it, though it should work with @WithAnonymousUser
    void register() throws Exception {
        // given
        RegisterRequest request = new RegisterRequest(
                "John",
                "Doe",
                "john.doe@gmail.com",
                "40580jkdjfJIJj",
                "EUR"
        );
        String requestContent = """
                {
                    "email": "john.doe@gmail.com",
                    "password": "40580jkdjfJIJj",
                    "firstName": "John",
                    "lastName": "Doe",
                    "currency": "EUR"
                }""";
        AuthenticationResponse response = new AuthenticationResponse(
                "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJleHAiOjE2ODU4NzQ3MzksImlhdCI6MTY4NTc4ODMzOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dfQ.G4axFV8xRkzcpLulzPXp-bRuRc3lQIs2vp_jkb6LxLw",
                new UserDto(
                        454L,
                        "John",
                        "Doe",
                        "john.doe@gmail.com",
                        Role.USER,
                        "EUR",
                        new ArrayList<>()
                )
        );
        String responseContent = """

                {
                    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbkBnbWFpbC5jb20iLCJleHAiOjE2ODU4NzQ3MzksImlhdCI6MTY4NTc4ODMzOSwiYXV0aG9yaXRpZXMiOlt7ImF1dGhvcml0eSI6IkFETUlOIn1dfQ.G4axFV8xRkzcpLulzPXp-bRuRc3lQIs2vp_jkb6LxLw",
                    "user": {
                        "id": 454,
                        "firstName": "John",
                        "lastName": "Doe",
                        "email": "john.doe@gmail.com",
                        "role": "USER",
                        "currency": "EUR",
                        "accountIds": []
                    }
                }""";
        given(authenticationService.register(request))
                .willReturn(response);

        // then
        mockMvc
                .perform(
                        post(ROOT_URL + "/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestContent)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(responseContent));
    }
}