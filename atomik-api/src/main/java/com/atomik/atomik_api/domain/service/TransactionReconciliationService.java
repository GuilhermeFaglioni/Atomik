package com.atomik.atomik_api.domain.service;

import com.atomik.atomik_api.domain.model.Transaction;

public interface TransactionReconciliationService {
    void reconcile(Transaction oldState, Transaction newState);

    void rollBack(Transaction oldState);
}
