package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class CreateTransferUseCase {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    public CreateTransferUseCase(TransactionRepository transactionRepository, AccountRepository accountRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionCreatedResponse execute(String userId, String categoryId, String sourceAccountId,
            String destinationAccountId, BigDecimal amount, String description, LocalDateTime date) {
        if (userRepository.findById(UUID.fromString(userId)).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (accountRepository.findById(UUID.fromString(sourceAccountId)).isEmpty()
                || accountRepository.findById(UUID.fromString(destinationAccountId)).isEmpty()) {
            throw new AccountNotFoundException("Account not found");
        }

        if (accountRepository.findById(UUID.fromString(sourceAccountId))
                .equals(accountRepository.findById(UUID.fromString(destinationAccountId)))) {
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }

        var transaction = Transaction.createTransfer(UUID.fromString(userId), UUID.fromString(categoryId),
                UUID.fromString(sourceAccountId), UUID.fromString(destinationAccountId), amount, description, date);
        transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    private TransactionCreatedResponse toResponse(Transaction transaction) {
        return new TransactionCreatedResponse(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), "Transfer created successfully");
    }

}
