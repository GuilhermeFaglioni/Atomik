package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.domain.exception.RecurringTransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.RecurringStatus;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class UpdateRecurringTransactionStatus {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;

    public UpdateRecurringTransactionStatus(RecurringTransactionRepository recurringTransactionRepository,
            UserRepository userRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RecurringResponseDTO execute(String userId, String id, RecurringStatus status) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        var recurringTransaction = recurringTransactionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RecurringTransactionNotFoundException("Recurring transaction not found"));
        if (!recurringTransaction.getUserId().toString().equals(userId)) {
            throw new UnauthorizedException("Unauthorized");
        }
        var updatedTransaction = recurringTransaction.updateStatus(recurringTransaction, status);
        recurringTransactionRepository.save(updatedTransaction);
        return toResponse(updatedTransaction);
    }

    private RecurringResponseDTO toResponse(com.atomik.atomik_api.domain.model.RecurringTransaction recurringTransaction) {
        String destId = recurringTransaction.getDestinationAccountId() != null
                ? recurringTransaction.getDestinationAccountId().toString()
                : null;
        return new RecurringResponseDTO(recurringTransaction.getId().toString(),
                recurringTransaction.getCategoryId().toString(), recurringTransaction.getSourceAccountId().toString(),
                destId, recurringTransaction.getAmount(),
                recurringTransaction.getDescription(), recurringTransaction.getType(),
                recurringTransaction.getStartDate(), recurringTransaction.getEndDate(),
                recurringTransaction.getNextDueDate(), recurringTransaction.getFrequency(),
                recurringTransaction.getStatus());
    }
}
