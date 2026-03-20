package com.reward.service;

import com.reward.dto.CustomerRewardSummaryDto;
import com.reward.exception.InvalidTransactionException;
import com.reward.exception.ResourceNotFoundException;
import com.reward.model.Transaction;
import com.reward.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RewardServiceImplTest {

    private TransactionRepository transactionRepository;
    private RewardServiceImpl rewardService;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        rewardService = new RewardServiceImpl(transactionRepository);
    }

    @Test
    void shouldReturnZeroPointsForAmountLessThanOrEqualTo50() {
        assertEquals(0, rewardService.calculateRewardPoints(new BigDecimal("40")));
        assertEquals(0, rewardService.calculateRewardPoints(new BigDecimal("50.00")));
    }

    @Test
    void shouldReturnCorrectPointsForAmountBetween51And100() {
        assertEquals(1, rewardService.calculateRewardPoints(new BigDecimal("51")));
        assertEquals(25, rewardService.calculateRewardPoints(new BigDecimal("75")));
        assertEquals(50, rewardService.calculateRewardPoints(new BigDecimal("100.00")));
    }

    @Test
    void shouldReturnCorrectPointsForAmountGreaterThan100() {
        assertEquals(52, rewardService.calculateRewardPoints(new BigDecimal("101")));
        assertEquals(90, rewardService.calculateRewardPoints(new BigDecimal("120")));
        assertEquals(250, rewardService.calculateRewardPoints(new BigDecimal("200")));
    }

    @Test
    void shouldThrowExceptionForNegativeAmount() {
        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> rewardService.calculateRewardPoints(new BigDecimal("-10"))
        );

        assertEquals("Transaction amount cannot be negative.", exception.getMessage());
    }

    @Test
    void shouldReturnCustomerSummaryById() {
        when(transactionRepository.findAllTransactions()).thenReturn(sampleTransactions());

        CustomerRewardSummaryDto summary = rewardService.getRewardSummaryByCustomerId(101L);

        assertNotNull(summary);
        assertEquals(101L, summary.getCustomerId());
        assertEquals("Ajay Kumar", summary.getCustomerName());
        assertTrue(summary.getTotalPoints() > 0);
    }

    @Test
    void shouldHandleMultipleTransactionsInSameMonth() {
        LocalDate now = LocalDate.now();
        List<Transaction> transactions = List.of(
                new Transaction(1L, 101L, "Ajay Kumar", new BigDecimal("120"), now),
                new Transaction(2L, 101L, "Ajay Kumar", new BigDecimal("80"), now)
        );
        when(transactionRepository.findAllTransactions()).thenReturn(transactions);

        CustomerRewardSummaryDto summary = rewardService.getRewardSummaryByCustomerId(101L);

        // 120 -> 50 + 20*2 = 90
        // 80 -> 80 - 50 = 30
        // Total = 120
        assertEquals(120, summary.getTotalPoints());
        assertEquals(1, summary.getMonthlyRewards().size());
    }

    @Test
    void shouldThrowExceptionForCustomerWithNoTransactionsInThreeMonthWindow() {
        LocalDate fourMonthsAgo = LocalDate.now().minusMonths(4);
        List<Transaction> transactions = List.of(
                new Transaction(1L, 101L, "Ajay Kumar", new BigDecimal("100"), fourMonthsAgo),
                new Transaction(2L, 102L, "Vidya Baligar", new BigDecimal("100"), LocalDate.now())
        );
        when(transactionRepository.findAllTransactions()).thenReturn(transactions);

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.getRewardSummaryByCustomerId(101L));
    }

    @Test
    void shouldThrowNotFoundForUnknownCustomer() {
        when(transactionRepository.findAllTransactions()).thenReturn(sampleTransactions());

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.getRewardSummaryByCustomerId(999L));
    }

    private List<Transaction> sampleTransactions() {
        LocalDate now = LocalDate.now();
        return List.of(
                new Transaction(1L, 101L, "Ajay Kumar", new BigDecimal("120"), now.minusMonths(2)),
                new Transaction(2L, 101L, "Ajay Kumar", new BigDecimal("75"), now.minusMonths(1)),
                new Transaction(3L, 101L, "Ajay Kumar", new BigDecimal("130"), now),
                new Transaction(4L, 102L, "Vidya Baligar", new BigDecimal("90"), now.minusMonths(2)),
                new Transaction(5L, 102L, "Vidya Baligar", new BigDecimal("150"), now.minusMonths(1)),
                new Transaction(6L, 102L, "Vidya Baligar", new BigDecimal("45"), now)
        );
    }
}