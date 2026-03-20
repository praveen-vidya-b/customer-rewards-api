package com.vidyab.rewards.service.impl;

import com.vidyab.rewards.dto.CustomerRewardSummaryDto;
import com.vidyab.rewards.exception.InvalidTransactionException;
import com.vidyab.rewards.exception.ResourceNotFoundException;
import com.vidyab.rewards.model.Transaction;
import com.vidyab.rewards.repository.TransactionRepository;
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
        assertEquals(0, rewardService.calculateRewardPoints(40));
        assertEquals(0, rewardService.calculateRewardPoints(50));
    }

    @Test
    void shouldReturnCorrectPointsForAmountBetween51And100() {
        assertEquals(1, rewardService.calculateRewardPoints(51));
        assertEquals(25, rewardService.calculateRewardPoints(75));
        assertEquals(50, rewardService.calculateRewardPoints(100));
    }

    @Test
    void shouldReturnCorrectPointsForAmountGreaterThan100() {
        assertEquals(52, rewardService.calculateRewardPoints(101));
        assertEquals(90, rewardService.calculateRewardPoints(120));
        assertEquals(250, rewardService.calculateRewardPoints(200));
    }

    @Test
    void shouldThrowExceptionForNegativeAmount() {
        InvalidTransactionException exception = assertThrows(
                InvalidTransactionException.class,
                () -> rewardService.calculateRewardPoints(-10)
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
    void shouldThrowNotFoundForUnknownCustomer() {
        when(transactionRepository.findAllTransactions()).thenReturn(sampleTransactions());

        assertThrows(ResourceNotFoundException.class,
                () -> rewardService.getRewardSummaryByCustomerId(999L));
    }

    private List<Transaction> sampleTransactions() {
        return List.of(
                new Transaction(1L, 101L, "Ajay Kumar", new BigDecimal("120"), LocalDate.of(2026, 10, 5)),
                new Transaction(2L, 101L, "Ajay Kumar", new BigDecimal("75"), LocalDate.of(2026, 11, 5)),
                new Transaction(3L, 101L, "Ajay Kumar", new BigDecimal("130"), LocalDate.of(2026, 12, 5)),
                new Transaction(4L, 102L, "Vidya Baligar", new BigDecimal("90"), LocalDate.of(2026, 10, 10)),
                new Transaction(5L, 102L, "Vidya Baligar", new BigDecimal("150"), LocalDate.of(2026, 11, 10)),
                new Transaction(6L, 102L, "Vidya Baligar", new BigDecimal("45"), LocalDate.of(2026, 12, 10))
        );
    }
}