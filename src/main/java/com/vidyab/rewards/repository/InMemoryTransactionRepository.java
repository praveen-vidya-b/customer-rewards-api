package com.vidyab.rewards.repository;

import com.vidyab.rewards.model.Transaction;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * In-memory repository with sample data to demonstrate the solution.
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    @Override
    public List<Transaction> findAllTransactions() {
        return List.of(
                // Customer 1 - Ajay
                new Transaction(1L, 101L, "Ajay Kumar", new BigDecimal("120"), LocalDate.of(2026, 10, 5)),
                new Transaction(2L, 101L, "Ajay Kumar", new BigDecimal("75"), LocalDate.of(2026, 10, 18)),
                new Transaction(3L, 101L, "Ajay Kumar", new BigDecimal("200"), LocalDate.of(2026, 11, 10)),
                new Transaction(4L, 101L, "Ajay Kumar", new BigDecimal("49"), LocalDate.of(2026, 12, 2)),
                new Transaction(5L, 101L, "Ajay Kumar", new BigDecimal("130"), LocalDate.of(2026, 12, 15)),

                // Customer 2 - Vidya
                new Transaction(6L, 102L, "Vidya Baligar", new BigDecimal("51"), LocalDate.of(2026, 10, 3)),
                new Transaction(7L, 102L, "Vidya Baligar", new BigDecimal("99"), LocalDate.of(2026, 11, 9)),
                new Transaction(8L, 102L, "Vidya Baligar", new BigDecimal("101"), LocalDate.of(2026, 11, 21)),
                new Transaction(9L, 102L, "Vidya Baligar", new BigDecimal("180"), LocalDate.of(2026, 12, 8)),
                new Transaction(10L, 102L, "Vidya Baligar", new BigDecimal("45"), LocalDate.of(2026, 12, 22)),

                // Customer 3 - Sagar
                new Transaction(11L, 103L, "Sagar", new BigDecimal("40"), LocalDate.of(2026, 10, 7)),
                new Transaction(12L, 103L, "Sagar", new BigDecimal("55"), LocalDate.of(2026, 11, 11)),
                new Transaction(13L, 103L, "Sagar", new BigDecimal("110"), LocalDate.of(2026, 12, 29))
        );
    }
}