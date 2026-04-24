package com.atomik.atomik_api.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atomik.atomik_api.application.dto.SyncRequestDTO;
import com.atomik.atomik_api.application.dto.SyncTransactionItemDTO;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SyncTransactionsUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CreateUniqueTransactionUseCase createUniqueTransactionUseCase;

    @Mock
    private CreateTransferUseCase createTransferUseCase;

    @Mock
    private UpdateTransactionUseCase updateTransactionUseCase;

    @Mock
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @InjectMocks
    private SyncTransactionsUseCase syncTransactionsUseCase;

    @Test
    @DisplayName("Should call delete use case with user id first and transaction id second")
    void shouldCallDeleteUseCaseWithUserIdFirstAndTransactionIdSecond() {
        UUID userId = UUID.randomUUID();
        String transactionId = UUID.randomUUID().toString();
        User user = new User(userId, "John", new com.atomik.atomik_api.domain.model.Email("john@test.com"), "hash",
                "BRL", LocalDateTime.now().minusDays(1));
        SyncRequestDTO request = new SyncRequestDTO(userId.toString(), List.of(
                new SyncTransactionItemDTO(transactionId, null, null, null, null, null, null, "EXPENSE", "DELETE")));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(accountRepository.findByUserId(userId)).thenReturn(List.of());

        var response = syncTransactionsUseCase.execute(request);

        verify(deleteTransactionUseCase).execute(userId.toString(), transactionId);
        assertEquals("SUCCESS", response.results().get(0).status());
    }
}
