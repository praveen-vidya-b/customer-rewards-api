package com.vidyab.rewards.repository;

import com.vidyab.rewards.model.Transaction;

import java.util.List;

/**
 * Repository interface for fetching transactions.
 */
public interface TransactionRepository {

    /**
     * Returns all available transactions from the data source.
     *
     * @return list of transactions;
     */
    List<Transaction> findAllTransactions();
}