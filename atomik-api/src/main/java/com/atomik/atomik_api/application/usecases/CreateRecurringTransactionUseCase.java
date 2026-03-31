package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.dto.CreateRecurringRequestDTO;
import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.exception.CategoryNotFoundException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.repository.CategoryRepository;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class CreateRecurringTransactionUseCase {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;

    public CreateRecurringTransactionUseCase(RecurringTransactionRepository recurringTransactionRepository,
            UserRepository userRepository, AccountRepository accountRepository,
            CategoryRepository categoryRepository) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public RecurringResponseDTO execute(CreateRecurringRequestDTO request) {
        userRepository.findById(UUID.fromString(request.userId()))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        categoryRepository.findById(UUID.fromString(request.categoryId()))
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        accountRepository.findById(UUID.fromString(request.sourceAccountId()))
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
        if (request.destinationAccountId() != null) {
            accountRepository.findById(UUID.fromString(request.destinationAccountId()))
                    .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));
            if (request.destinationAccountId().equals(request.sourceAccountId())) {
                throw new IllegalArgumentException("Destination account must be different from source account");
            }
        }

        var recurringTransaction = RecurringTransaction.create(UUID.fromString(request.userId()),
                UUID.fromString(request.categoryId()), UUID.fromString(request.sourceAccountId()),
                request.destinationAccountId() != null ? UUID.fromString(request.destinationAccountId()) : null,
                request.amount(), request.description(), request.type(), request.frequency(), request.startDate(),
                request.endDate());

        recurringTransactionRepository.save(recurringTransaction);

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
