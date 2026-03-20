package com.vidyab.rewards.service.impl;

import com.vidyab.rewards.dto.CustomerRewardSummaryDto;
import com.vidyab.rewards.dto.MonthlyRewardDto;
import com.vidyab.rewards.exception.InvalidTransactionException;
import com.vidyab.rewards.exception.ResourceNotFoundException;
import com.vidyab.rewards.model.Transaction;
import com.vidyab.rewards.repository.TransactionRepository;
import com.vidyab.rewards.service.RewardService;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Default implementation for reward calculation logic.
 */
@Service
public class RewardServiceImpl implements RewardService {

    private final TransactionRepository transactionRepository;

    public RewardServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Retrieves a three-month reward summary for the specified customer.
     *
     * @param customerId the ID of the customer
     * @return a summary containing monthly reward points and the total
     * @throws ResourceNotFoundException       if the customer or their recent transactions are not found
     * @throws InvalidTransactionException     if transaction data is null/invalid
     */
    @Override
    public CustomerRewardSummaryDto getRewardSummaryByCustomerId(Long customerId) {
        List<Transaction> transactions = validateTransactions(transactionRepository.findAllTransactions());

        List<Transaction> customerTransactions = transactions.stream()
                .filter(t -> t.getCustomerId().equals(customerId))
                .toList();

        if (customerTransactions.isEmpty()) {
            throw new ResourceNotFoundException("Customer not found for id: " + customerId);
        }

        List<YearMonth> lastThreeMonths = getLastThreeMonths(transactions);

        List<Transaction> filteredCustomerTransactions = customerTransactions.stream()
                .filter(t -> lastThreeMonths.contains(YearMonth.from(t.getTransactionDate())))
                .toList();

        return buildCustomerSummary(filteredCustomerTransactions, lastThreeMonths);
    }

    /**
     * Reward formula:
     * 1 point for every dollar between 50 and 100
     * 2 points for every dollar above 100
     */
    @Override
    public long calculateRewardPoints(double amount) {
        if (amount < 0) {
            throw new InvalidTransactionException("Transaction amount cannot be negative.");
        }

        long roundedAmount = (long) Math.floor(amount);

        if (roundedAmount <= 50) {
            return 0;
        } else if (roundedAmount <= 100) {
            return roundedAmount - 50;
        } else {
            return 50 + ((roundedAmount - 100) * 2);
        }
    }

    private CustomerRewardSummaryDto buildCustomerSummary(List<Transaction> customerTransactions, List<YearMonth> lastThreeMonths) {
        if (customerTransactions.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found for customer in last three months.");
        }

        Transaction firstTransaction = customerTransactions.get(0);
        Long customerId = firstTransaction.getCustomerId();
        String customerName = firstTransaction.getCustomerName();

        Map<YearMonth, Long> monthlyPointsMap = new LinkedHashMap<>();
        for (YearMonth ym : lastThreeMonths) {
            monthlyPointsMap.put(ym, 0L);
        }

        for (Transaction transaction : customerTransactions) {
            YearMonth yearMonth = YearMonth.from(transaction.getTransactionDate());
            long points = calculateRewardPoints(transaction.getAmount().doubleValue());
            monthlyPointsMap.put(yearMonth, monthlyPointsMap.getOrDefault(yearMonth, 0L) + points);
        }

        List<MonthlyRewardDto> monthlyRewards = new ArrayList<>();
        long totalPoints = 0;

        for (Map.Entry<YearMonth, Long> entry : monthlyPointsMap.entrySet()) {
            String monthLabel = entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                    + " " + entry.getKey().getYear();
            long points = entry.getValue();
            monthlyRewards.add(new MonthlyRewardDto(monthLabel, points));
            totalPoints += points;
        }

        return new CustomerRewardSummaryDto(customerId, customerName, monthlyRewards, totalPoints);
    }

    /**
     * Dynamically derives the latest three months from transaction data.
     * This avoids hardcoding month names.
     */
    private List<YearMonth> getLastThreeMonths(List<Transaction> transactions) {
        YearMonth maxMonth = transactions.stream()
                .map(t -> YearMonth.from(t.getTransactionDate()))
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new InvalidTransactionException("No transaction months available."));

        return List.of(
                maxMonth.minusMonths(2),
                maxMonth.minusMonths(1),
                maxMonth
        );
    }

    private List<Transaction> validateTransactions(List<Transaction> transactions) {
        if (transactions == null) {
            throw new InvalidTransactionException("Transaction list cannot be null.");
        }

        for (Transaction transaction : transactions) {
            if (transaction.getCustomerId() == null) {
                throw new InvalidTransactionException("Customer id cannot be null.");
            }
            if (transaction.getCustomerName() == null || transaction.getCustomerName().isBlank()) {
                throw new InvalidTransactionException("Customer name cannot be null or blank.");
            }
            if (transaction.getAmount() == null) {
                throw new InvalidTransactionException("Transaction amount cannot be null.");
            }
            if (transaction.getAmount().doubleValue() < 0) {
                throw new InvalidTransactionException("Transaction amount cannot be negative.");
            }
            if (transaction.getTransactionDate() == null) {
                throw new InvalidTransactionException("Transaction date cannot be null.");
            }
        }

        return transactions;
    }
}