package com.atomik.atomik_api.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class GetRecurringTransactionsByUserUseCase {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;

    public GetRecurringTransactionsByUserUseCase(RecurringTransactionRepository recurringTransactionRepository,
            UserRepository userRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.userRepository = userRepository;
    }

    public List<RecurringResponseDTO> execute(String userId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        var recurringTransactions = recurringTransactionRepository.findByUserId(UUID.fromString(userId));

        return recurringTransactions.stream().map(this::toResponse).toList();
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
