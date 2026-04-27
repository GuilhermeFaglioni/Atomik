package com.atomik.atomik_api.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

import com.atomik.atomik_api.application.usecases.CreateTransferUseCase;
import com.atomik.atomik_api.application.usecases.CreateUniqueTransactionUseCase;
import com.atomik.atomik_api.application.usecases.DeleteTransactionUseCase;
import com.atomik.atomik_api.application.usecases.GetTransactionUseCase;
import com.atomik.atomik_api.application.usecases.ListUserTransactionUseCase;
import com.atomik.atomik_api.application.usecases.UpdateTransactionUseCase;
import com.atomik.atomik_api.domain.service.TokenService;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateUniqueTransactionUseCase createUniqueTransactionUseCase;

    @MockBean
    private CreateTransferUseCase createTransferUseCase;

    @MockBean
    private GetTransactionUseCase getTransactionUseCase;

    @MockBean
    private ListUserTransactionUseCase listUserTransactionUseCase;

    @MockBean
    private UpdateTransactionUseCase updateTransactionUseCase;

    @MockBean
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @MockBean
    private AuthenticatedUserService authenticatedUserService;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Should return 400 with validation payload when amount is missing")
    void shouldReturn400WhenAmountIsMissing() throws Exception {
        when(authenticatedUserService.requireCurrentUser(any(), anyString())).thenReturn("user-id");

        String json = """
                {
                  "userId": "user-id",
                  "accountId": "account-id",
                  "categoryId": "category-id",
                  "description": "Lunch",
                  "amount": null,
                  "date": "2026-04-26T10:00:00",
                  "type": "EXPENSE"
                }
                """;

        mockMvc.perform(post("/transactions/unique")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Amount is required"));
    }

    @Test
    @DisplayName("Should return 400 when update payload has invalid enum or date body")
    void shouldReturn400WhenUpdatePayloadIsMalformed() throws Exception {
        when(authenticatedUserService.requireCurrentUser(any(), anyString())).thenReturn("user-id");

        String json = """
                {
                  "categoryId": "category-id",
                  "sourceAccountId": "account-id",
                  "destinationAccountId": null,
                  "amount": 12.50,
                  "description": "Lunch",
                  "date": "not-a-date",
                  "type": "WRONG"
                }
                """;

        mockMvc.perform(put("/transactions/user-id/tx-id")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Invalid request body"));
    }
}
