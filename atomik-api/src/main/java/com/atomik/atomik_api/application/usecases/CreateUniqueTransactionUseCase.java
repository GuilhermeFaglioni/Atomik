package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.dto.TransactionCreatedResponse;
import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.AuditLog;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.AuditLogRepository;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class CreateUniqueTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    public CreateUniqueTransactionUseCase(TransactionRepository transactionRepository,
            AccountRepository accountRepository, UserRepository userRepository, AuditLogRepository auditLogRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public TransactionCreatedResponse execute(String userId,
            String categoryId,
            String accountId,
            BigDecimal amount,
            String description,
            LocalDateTime date,
            TransactionType type) {
        if (userRepository.findById(UUID.fromString(userId)).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (accountRepository.findById(UUID.fromString(userId)).isEmpty()) {
            throw new AccountNotFoundException("Account not found");
        }
        if (type.equals(TransactionType.TRANSFER)) {
            throw new IllegalArgumentException("Use createTransfer for transfer type");
        }
        var transaction = Transaction.createSingleEntry(UUID.fromString(userId), UUID.fromString(categoryId),
                UUID.fromString(accountId), amount, description, date, type);

        AuditLog auditLog = AuditLog.createNewAuditLog(transaction.getId(), "New Unique Transaction", "N/A",
                transaction.getAmount().toString());
        auditLogRepository.save(auditLog);
        transactionRepository.save(transaction);
        return toResponse(transaction);

    }

    private TransactionCreatedResponse toResponse(Transaction transaction) {
        return new TransactionCreatedResponse(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), "Transaction created successfully");
    }

}
