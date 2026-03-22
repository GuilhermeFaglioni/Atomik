package com.atomik.atomik_api.application.usecases;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.TransactionResponseDTO;
import com.atomik.atomik_api.domain.exception.TransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class UpdateTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public UpdateTransactionUseCase(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public TransactionResponseDTO execute(String transactionId, String userId, String categoryId,
            String sourceAccountId,
            String destinationAccountId,
            BigDecimal amount, String description, LocalDateTime date, TransactionType type) {
        Transaction sourceTransaction = transactionRepository.findById(UUID.fromString(transactionId))
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getId().equals(sourceTransaction.getUserId())) {
            throw new UnauthorizedException("User not authorized to access this transaction");
        }
        var updatedTransaction = new Transaction(sourceTransaction.getId(), sourceTransaction.getUserId(),
                UUID.fromString(categoryId), UUID.fromString(sourceAccountId), UUID.fromString(destinationAccountId),
                amount, description, date, type, sourceTransaction.getSyncStatus(), sourceTransaction.getCreatedAt());
        updatedTransaction.validate();

        var savedTransaction = transactionRepository.update(updatedTransaction)
                .orElseThrow(() -> new RuntimeException("Error updating transaction"));
        return toResponse(savedTransaction);
    }

    private TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), transaction.getDate().toString());
    }
}
