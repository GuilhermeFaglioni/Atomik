package com.atomik.atomik_api.application.usecases;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atomik.atomik_api.application.service.FinancialResourceOwnershipService;
import com.atomik.atomik_api.application.dto.CreateRecurringRequestDTO;
import com.atomik.atomik_api.application.dto.RecurringResponseDTO;
import com.atomik.atomik_api.domain.model.RecurringTransaction;
import com.atomik.atomik_api.domain.repository.RecurringTransactionRepository;

@Service
public class CreateRecurringTransactionUseCase {
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final FinancialResourceOwnershipService financialResourceOwnershipService;

    public CreateRecurringTransactionUseCase(RecurringTransactionRepository recurringTransactionRepository,
            FinancialResourceOwnershipService financialResourceOwnershipService) {
        this.recurringTransactionRepository = recurringTransactionRepository;
        this.financialResourceOwnershipService = financialResourceOwnershipService;
    }

    @Transactional
    public RecurringResponseDTO execute(CreateRecurringRequestDTO request) {
        UUID parsedUserId = financialResourceOwnershipService.requireExistingUser(request.userId());
        financialResourceOwnershipService.requireOwnedCategory(parsedUserId, request.categoryId());
        financialResourceOwnershipService.requireOwnedAccount(parsedUserId, request.sourceAccountId(),
                "Source account not found");
        if (request.destinationAccountId() != null) {
            financialResourceOwnershipService.requireOwnedAccount(parsedUserId, request.destinationAccountId(),
                    "Destination account not found");
            if (request.destinationAccountId().equals(request.sourceAccountId())) {
                throw new IllegalArgumentException("Destination account must be different from source account");
            }
        }

        var recurringTransaction = RecurringTransaction.create(parsedUserId,
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
