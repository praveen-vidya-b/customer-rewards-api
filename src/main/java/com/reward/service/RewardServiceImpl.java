package com.reward.service;

import com.reward.constant.RewardsConstant;
import com.reward.dto.CustomerRewardSummaryDto;
import com.reward.dto.MonthlyRewardDto;
import com.reward.exception.InvalidTransactionException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.model.Transaction;
import com.reward.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
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

        YearMonth currentMonth = YearMonth.now();
        List<YearMonth> lastThreeMonths = List.of(
                currentMonth.minusMonths(2),
                currentMonth.minusMonths(1),
                currentMonth
        );

        List<Transaction> filteredCustomerTransactions = customerTransactions.stream()
                .filter(t -> lastThreeMonths.contains(YearMonth.from(t.getTransactionDate())))
                .toList();

        return buildCustomerSummary(filteredCustomerTransactions, lastThreeMonths, customerId);
    }

    /**
     * Reward formula:
     * 1 point for every dollar between 50 and 100
     * 2 points for every dollar above 100
     */
    @Override
    public long calculateRewardPoints(BigDecimal amount) {
        if (amount == null) {
            throw new InvalidTransactionException("Transaction amount cannot be null.");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransactionException("Transaction amount cannot be negative.");
        }

        if (amount.compareTo(RewardsConstant.FIFTY) <= 0) {
            return 0;
        } else if (amount.compareTo(RewardsConstant.HUNDRED) <= 0) {
            return amount.subtract(RewardsConstant.FIFTY).longValue();
        } else {
            return 50 + (amount.subtract(RewardsConstant.HUNDRED).multiply(new BigDecimal("2")).longValue());
        }
    }

    private CustomerRewardSummaryDto buildCustomerSummary(List<Transaction> customerTransactions, List<YearMonth> lastThreeMonths, Long requestedCustomerId) {
        if (customerTransactions.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found for customer in last three months.");
        }

        Transaction firstTransaction = customerTransactions.get(0);
        String customerName = firstTransaction.getCustomerName();

        Map<YearMonth, Long> monthlyPointsMap = new LinkedHashMap<>();
        for (YearMonth ym : lastThreeMonths) {
            monthlyPointsMap.put(ym, 0L);
        }

        for (Transaction transaction : customerTransactions) {
            YearMonth yearMonth = YearMonth.from(transaction.getTransactionDate());
            long points = calculateRewardPoints(transaction.getAmount());
            monthlyPointsMap.put(yearMonth, monthlyPointsMap.getOrDefault(yearMonth, 0L) + points);
        }

        List<MonthlyRewardDto> monthlyRewards = new ArrayList<>();
        long totalPoints = 0;

        for (Map.Entry<YearMonth, Long> entry : monthlyPointsMap.entrySet()) {
            long points = entry.getValue();
            if (points > 0) {
                String monthLabel = entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                        + " " + entry.getKey().getYear();
                monthlyRewards.add(new MonthlyRewardDto(monthLabel, points));
            }
            totalPoints += points;
        }

        if (monthlyRewards.isEmpty() && totalPoints == 0) {
            throw new ResourceNotFoundException("No transactions found for customer in last three months.");
        }

        return new CustomerRewardSummaryDto(requestedCustomerId, customerName, monthlyRewards, totalPoints);
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