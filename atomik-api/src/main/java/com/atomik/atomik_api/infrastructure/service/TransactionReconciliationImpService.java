package com.atomik.atomik_api.infrastructure.service;

import org.springframework.stereotype.Service;

import com.atomik.atomik_api.domain.exception.AccountNotFoundException;
import com.atomik.atomik_api.domain.model.Account;
import com.atomik.atomik_api.domain.model.Transaction;
import com.atomik.atomik_api.domain.model.TransactionType;
import com.atomik.atomik_api.domain.repository.AccountRepository;
import com.atomik.atomik_api.domain.service.TransactionReconciliationService;

@Service
public class TransactionReconciliationImpService implements TransactionReconciliationService {
    private final AccountRepository accountRepository;

    public TransactionReconciliationImpService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void reconcile(Transaction oldState, Transaction newState) {
        rollBack(oldState);
        apply(newState);
    }

    @Override
    public void apply(Transaction state) {
        Account sourceAcc = accountRepository.findById(state.getSourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
        if (state.getType() == TransactionType.TRANSFER) {
            Account destAcc = accountRepository.findById(state.getDestinationAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));
            accountRepository.update(sourceAcc.withdraw(state.getAmount()));
            accountRepository.update(destAcc.deposit(state.getAmount()));
        } else if (state.getType() == TransactionType.EXPENSE) {
            accountRepository.update(sourceAcc.withdraw(state.getAmount()));
        } else if (state.getType() == TransactionType.REVENUE) {
            accountRepository.update(sourceAcc.deposit(state.getAmount()));
        }
    }

    @Override
    public void rollBack(Transaction oldState) {
        Account sourceAccount = accountRepository.findById(oldState.getSourceAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        if (oldState.getType() == TransactionType.TRANSFER) {
            Account destinationAccount = accountRepository.findById(oldState.getDestinationAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            accountRepository.update(sourceAccount.deposit(oldState.getAmount()));
            accountRepository.update(destinationAccount.withdraw(oldState.getAmount()));
        } else if (oldState.getType() == TransactionType.EXPENSE) {
            accountRepository.update(sourceAccount.deposit(oldState.getAmount()));
        } else if (oldState.getType() == TransactionType.REVENUE) {
            accountRepository.update(sourceAccount.withdraw(oldState.getAmount()));
        }
    }
}
