package com.atomik.atomik_api.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.atomik.atomik_api.application.usecases.CreateAccountUseCase;
import com.atomik.atomik_api.application.usecases.DeleteAccountUseCase;
import com.atomik.atomik_api.application.usecases.GetAccountUseCase;
import com.atomik.atomik_api.application.usecases.ListAccountsUseCase;
import com.atomik.atomik_api.application.usecases.UpdateAccountUseCase;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.service.TokenService;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateAccountUseCase createAccountUseCase;

    @MockBean
    private DeleteAccountUseCase deleteAccountUseCase;

    @MockBean
    private GetAccountUseCase getAccountUseCase;

    @MockBean
    private ListAccountsUseCase listAccountsUseCase;

    @MockBean
    private UpdateAccountUseCase updateAccountUseCase;

    @MockBean
    private AuthenticatedUserService authenticatedUserService;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Should return 401 when authenticated user tries to access another user account list")
    void shouldReturn401WhenAuthenticatedUserTriesToAccessAnotherUserAccountList() throws Exception {
        when(authenticatedUserService.requireCurrentUser(any(), anyString()))
                .thenThrow(new UnauthorizedException("You do not have permission to access this resource"));

        mockMvc.perform(get("/accounts/other-user"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should return 400 when account type is invalid enum")
    void shouldReturn400WhenAccountTypeIsInvalidEnum() throws Exception {
        when(authenticatedUserService.requireCurrentUser(any(), anyString())).thenReturn("user-id");

        String json = """
                {
                  "userId": "user-id",
                  "name": "Wallet",
                  "type": "WRONG",
                  "currency": "BRL"
                }
                """;

        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }
}
