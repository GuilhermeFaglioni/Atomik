package com.atomik.atomik_api.application.usecases;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.AccountBalanceDTO;
import com.atomik.atomik_api.application.dto.SyncRequestDTO;
import com.atomik.atomik_api.application.dto.SyncResponseDTO;
import com.atomik.atomik_api.application.dto.SyncResultDTO;
import com.atomik.atomik_api.application.dto.SyncTransactionItemDTO;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class SyncTransactionsUseCase {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CreateUniqueTransactionUseCase createUniqueTransactionUseCase;
    private final CreateTransferUseCase createTransferUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;

    public SyncTransactionsUseCase(UserRepository userRepository, AccountRepository accountRepository,
            CreateUniqueTransactionUseCase createUniqueTransactionUseCase, CreateTransferUseCase createTransferUseCase,
            UpdateTransactionUseCase updateTransactionUseCase, DeleteTransactionUseCase deleteTransactionUseCase) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.createUniqueTransactionUseCase = createUniqueTransactionUseCase;
        this.createTransferUseCase = createTransferUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
    }

    public SyncResponseDTO execute(SyncRequestDTO request) {
        User user = userRepository.findById(UUID.fromString(request.userId()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<SyncResultDTO> results = new ArrayList<>();

        for (SyncTransactionItemDTO item : request.transactions()) {
            try {
                processItem(user, item);
                results.add(new SyncResultDTO(item.id(), "SUCCESS", "Transaction synced successfully"));
            } catch (Exception e) {
                results.add(new SyncResultDTO(item.id(), "ERROR", e.getMessage()));
            }
        }

        List<AccountBalanceDTO> balances = accountRepository.findByUserId(user.getId())
                .stream()
                .map(account -> new AccountBalanceDTO(account.getId().toString(), account.getBalance()))
                .toList();

        return new SyncResponseDTO(results, balances);
    }

    private void processItem(User user, SyncTransactionItemDTO item) {
        switch (item.operationType()) {
            case "CREATE":
                if ("TRANSFER".equalsIgnoreCase(item.type())) {
                    createTransferUseCase.execute(user.getId().toString(), item.categoryId(), item.sourceAccountId(),
                            item.destinationAccountId(), item.amount(), item.description(), item.date());
                } else {
                    createUniqueTransactionUseCase.execute(user.getId().toString(), item.categoryId(),
                            item.sourceAccountId(), item.amount(), item.description(), item.date(),
                            TransactionType.valueOf(item.type()));
                }
                break;
            case "UPDATE":
                updateTransactionUseCase.execute(item.id(), user.getId().toString(), item.categoryId(),
                        item.sourceAccountId(), item.destinationAccountId(), item.amount(), item.description(),
                        item.date(), TransactionType.valueOf(item.type()));
                break;
            case "DELETE":
                deleteTransactionUseCase.execute(user.getId().toString(), item.id());
                break;
            default:
                throw new IllegalArgumentException("Invalid operation type: " + item.operationType());
        }
    }
}
