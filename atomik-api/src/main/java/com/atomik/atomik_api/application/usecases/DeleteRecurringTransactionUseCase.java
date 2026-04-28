package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.domain.exception.RecurringTransactionNotFoundException;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class DeleteRecurringTransactionUseCase {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;

    public DeleteRecurringTransactionUseCase(RecurringTransactionRepository recurringTransactionRepository,
            UserRepository userRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void execute(String userId, String recurringTransactionId) {
        userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        var recurringTransaction = recurringTransactionRepository.findById(UUID.fromString(recurringTransactionId))
                .orElseThrow(() -> new RecurringTransactionNotFoundException("Recurring transaction not found"));

        if (!recurringTransaction.getUserId().equals(UUID.fromString(userId))) {
            throw new UnauthorizedException("User not authorized to delete this recurring transaction");
        }

        recurringTransactionRepository.delete(recurringTransaction.getId());
    }
}
