package com.babakjan.moneybag.controller;

import com.babakjan.moneybag.dto.account.CreateAccountRequest;
import com.babakjan.moneybag.dto.account.UpdateAccountRequest;
import com.babakjan.moneybag.entity.Account;
import com.babakjan.moneybag.entity.User;
import com.babakjan.moneybag.service.AccountService;
import com.babakjan.moneybag.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@Import(JwtService.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AccountService accountService;
    private final String ROOT_URL = "/accounts";

    @Test
    @WithAnonymousUser
    void testGetAllAnonymousUser() throws Exception {
        // given
        when(accountService.getAll())
                .thenReturn(new ArrayList<>());

        // then
        mockMvc
                .perform(get(ROOT_URL))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "john.doe@gmail.com", roles = "USER")
    void testGetAllUser() throws Exception {
        // given
        when(accountService.getAll())
                .thenThrow(AccessDeniedException.class);

        // then
        mockMvc
                .perform(get(ROOT_URL))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllAdmin() throws Exception {
        // given
        User user = User.builder()
                .id(43L)
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();

        Account frankAccount = Account.builder()
                .id(1L)
                .name("Frank account")
                .currency("CHF")
                .balance(5434.98)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(frankAccount);

        Account poundAccount = Account.builder()
                .id(2L)
                .name("Pound account")
                .currency("GBP")
                .balance(4325.0)
                .color("#fff")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(poundAccount);

        when(accountService.getAll())
                .thenReturn(List.of(frankAccount, poundAccount));

        // then
        mockMvc
                .perform(get(ROOT_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser
    void getAccountById() throws Exception {
        // given
        User user = User.builder()
                .id(43L)
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();

        Long accountId = 21L;
        Account frankAccount = Account.builder()
                .id(accountId)
                .name("Frank account")
                .currency("CHF")
                .balance(5434.98)
                .color("#333388")
                .icon("mdi-money")
                .records(new ArrayList<>())
                .includeInStatistic(true)
                .user(user)
                .build();
        user.addAccount(frankAccount);

        given(accountService.getById(accountId))
                .willReturn(frankAccount);

        // then
        mockMvc
                .perform(get(ROOT_URL + "/{id}", accountId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void testCreateAccount() throws Exception {
        // given
        Long userId = 43L;
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();
        CreateAccountRequest request = new CreateAccountRequest(
                "Savings",
                "USD",
                999.0,
                "#fff",
                "mdi-cash",
                true,
                userId
        );
        String requestContent =
                """
                        {
                            "name": "Savings",
                            "currency": "USD",
                            "balance": 999.0,
                            "color": "#fff",
                            "icon": "mdi-cash",
                            "includeInStatistic": true,
                            "userId": 43
                        }""";
        Account account = new Account(
                1L,
                "Savings",
                "USD",
                999.0,
                "#fff",
                "mdi-cash",
                true,
                new ArrayList<>(),
                user
        );
        String responseContent =
                """
                        {
                          "id": 1,
                          "name": "Savings",
                          "currency": "USD",
                          "balance": 999.0,
                          "color": "#fff",
                          "icon": "mdi-cash",
                          "includeInStatistic": true,
                          "recordIds": [],
                          "userId": 43,
                          "incomes": null,
                          "expenses": null
                        }""";

        given(accountService.save(request))
                .willReturn(account);

        // then
        mockMvc
                .perform(
                        post(ROOT_URL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestContent)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(responseContent));
    }

    @Test
    @WithMockUser
    void testDeleteById() throws Exception {
        Long accountId = 43545L;

        // then
        mockMvc
                .perform(
                        delete(ROOT_URL + "/{id}", accountId)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void updateAccount() throws Exception {
        // given
        Long userId = 43L;
        User user = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .accounts(new ArrayList<>())
                .build();
        Long accountId = 9087L;
        UpdateAccountRequest request = new UpdateAccountRequest(
                null,
                "savings",
                1500.0,
                null,
                null,
                false,
                userId
        );
        String requestContent =
                """
                        {
                            "name": "savings",
                            "balance": 1500.0,
                            "includeInStatistic": false,
                            "userId": 43
                        }""";
        Account account = new Account(
                1L,
                "savings",
                "USD",
                1500.0,
                "#fff",
                "mdi-cash",
                false,
                new ArrayList<>(),
                user
        );
        String responseContent =
                """
                         {
                          "id": 1,
                          "name": "savings",
                          "currency": "USD",
                          "balance": 1500.0,
                          "color": "#fff",
                          "icon": "mdi-cash",
                          "includeInStatistic": false,
                          "recordIds": [],
                          "userId": 43,
                          "incomes": null,
                          "expenses": null
                        }""";

        given(accountService.update(accountId, request))
                .willReturn(account);

        // then
        mockMvc
                .perform(
                        put(ROOT_URL + "/{id}", accountId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(requestContent)
                                .with(csrf())
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(responseContent));
    }
}