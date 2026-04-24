package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.domain.exception.RecurringTransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class GetRecurringTransactionById {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;

    public GetRecurringTransactionById(RecurringTransactionRepository recurringTransactionRepository,
            UserRepository userRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.userRepository = userRepository;
    }

    public RecurringResponseDTO execute(String id, String userId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        var recurringTransaction = recurringTransactionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RecurringTransactionNotFoundException("Recurring transaction not found"));

        if (!recurringTransaction.getUserId().equals(UUID.fromString(userId))) {
            throw new UnauthorizedException("User not authorized to access this transaction");
        }

        return toResponse(recurringTransaction);
    }

    private RecurringResponseDTO toResponse(RecurringTransaction recurringTransaction) {
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
