package com.atomik.atomik_api.application.usecases;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.application.dto.TransactionResponseDTO;
import com.atomik.atomik_api.domain.exception.UnauthorizedException;
import com.atomik.atomik_api.domain.exception.UserNotFoundException;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.User;
import com.atomik.atomik_api.domain.repository.TransactionRepository;
import com.atomik.atomik_api.domain.repository.UserRepository;

@Service
public class ListUserTransactionUseCase {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ListUserTransactionUseCase(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<TransactionResponseDTO> execute(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Transaction> transactions = transactionRepository.findByUserId(UUID.fromString(userId));
        if (!transactions.isEmpty() && !user.getId().equals(transactions.get(0).getUserId())) {
            throw new UnauthorizedException("User not authorized to access this transaction");
        }

        return transactions.stream().map(this::toResponse).toList();
    }

    private TransactionResponseDTO toResponse(Transaction transaction) {
        return new TransactionResponseDTO(transaction.getId().toString(), transaction.getType().toString(),
                transaction.getAmount(), transaction.getDescription(), transaction.getDate().toString());
    }
}
