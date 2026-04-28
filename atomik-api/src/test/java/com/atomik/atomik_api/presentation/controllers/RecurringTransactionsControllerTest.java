package com.atomik.atomik_api.presentation.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.application.usecases.CreateRecurringTransactionUseCase;
import com.atomik.atomik_api.application.usecases.DeleteRecurringTransactionUseCase;
import com.atomik.atomik_api.application.usecases.GetRecurringTransactionById;
import com.atomik.atomik_api.application.usecases.GetRecurringTransactionsByUserUseCase;
import com.atomik.atomik_api.application.usecases.UpdateRecurringTransactionStatus;
import com.atomik.atomik_api.domain.model.RecurringFrequency;
import com.atomik.atomik_api.domain.model.RecurringStatus;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.service.TokenService;
import com.atomik.atomik_api.presentation.security.AuthenticatedUserService;

@WebMvcTest(RecurringTransactionsController.class)
@AutoConfigureMockMvc(addFilters = false)
class RecurringTransactionsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateRecurringTransactionUseCase createRecurringTransactionUseCase;

    @MockBean
    private GetRecurringTransactionsByUserUseCase getRecurringTransactionsByUserUseCase;

    @MockBean
    private GetRecurringTransactionById getRecurringTransactionById;

    @MockBean
    private UpdateRecurringTransactionStatus updateRecurringTransactionStatus;

    @MockBean
    private DeleteRecurringTransactionUseCase deleteRecurringTransactionUseCase;

    @MockBean
    private AuthenticatedUserService authenticatedUserService;

    @MockBean
    private TokenService tokenService;

    @Test
    @DisplayName("Should create recurring transaction using authenticated user")
    void shouldCreateRecurringTransactionUsingAuthenticatedUser() throws Exception {
        when(authenticatedUserService.getAuthenticatedUserId(any())).thenReturn("auth-user-id");
        when(createRecurringTransactionUseCase.execute(any())).thenReturn(responseDto("rec-1", RecurringStatus.ACTIVE));

        String json = """
                {
                  "userId": "payload-user-id",
                  "categoryId": "category-id",
                  "sourceAccountId": "source-account-id",
                  "amount": 25.00,
                  "description": "Netflix",
                  "type": "EXPENSE",
                  "startDate": "2026-04-27T10:00:00",
                  "frequency": "MONTHLY"
                }
                """;

        mockMvc.perform(post("/recurring-transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("rec-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should list recurring transactions for authenticated user")
    void shouldListRecurringTransactionsForAuthenticatedUser() throws Exception {
        when(authenticatedUserService.getAuthenticatedUserId(any())).thenReturn("auth-user-id");
        when(getRecurringTransactionsByUserUseCase.execute("auth-user-id"))
                .thenReturn(List.of(responseDto("rec-1", RecurringStatus.ACTIVE)));

        mockMvc.perform(get("/recurring-transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("rec-1"));
    }

    @Test
    @DisplayName("Should update recurring transaction status")
    void shouldUpdateRecurringTransactionStatus() throws Exception {
        when(authenticatedUserService.getAuthenticatedUserId(any())).thenReturn("auth-user-id");
        when(updateRecurringTransactionStatus.execute(anyString(), anyString(), any()))
                .thenReturn(responseDto("rec-1", RecurringStatus.PAUSED));

        String json = """
                {
                  "status": "PAUSED"
                }
                """;

        mockMvc.perform(patch("/recurring-transactions/rec-1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }

    @Test
    @DisplayName("Should delete recurring transaction")
    void shouldDeleteRecurringTransaction() throws Exception {
        when(authenticatedUserService.getAuthenticatedUserId(any())).thenReturn("auth-user-id");

        mockMvc.perform(delete("/recurring-transactions/rec-1"))
                .andExpect(status().isNoContent());

        verify(deleteRecurringTransactionUseCase).execute("auth-user-id", "rec-1");
    }

    private RecurringResponseDTO responseDto(String id, RecurringStatus status) {
        return new RecurringResponseDTO(id, "category-id", "source-account-id", null, BigDecimal.valueOf(25.00),
                "Netflix", TransactionType.EXPENSE, LocalDateTime.parse("2026-04-27T10:00:00"), null,
                LocalDateTime.parse("2026-04-27T10:00:00"), RecurringFrequency.MONTHLY, status);
    }
}
