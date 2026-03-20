package com.reward.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reward.model.Transaction;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * In-memory repository that loads sample data from a JSON file and uses relative dates.
 */
@Repository
public class InMemoryTransactionRepository implements TransactionRepository {

    @Value("classpath:data/transactions.json")
    private Resource transactionsResource;

    private final ObjectMapper objectMapper;
    private List<Transaction> transactions = new ArrayList<>();

    public InMemoryTransactionRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    public void init() throws IOException {
        List<Map<String, Object>> data = objectMapper.readValue(
                transactionsResource.getInputStream(),
                new TypeReference<>() {}
        );

        LocalDate now = LocalDate.now();

        this.transactions = data.stream().map(map -> {
            Transaction t = new Transaction();
            t.setId(((Number) map.get("id")).longValue());
            t.setCustomerId(((Number) map.get("customerId")).longValue());
            t.setCustomerName((String) map.get("customerName"));
            t.setAmount(new BigDecimal(map.get("amount").toString()));
            
            int monthsAgo = (int) map.get("monthsAgo");
            int dayOfMonth = (int) map.get("dayOfMonth");
            
            LocalDate date = now.minusMonths(monthsAgo);
            int lastDay = date.lengthOfMonth();
            t.setTransactionDate(date.withDayOfMonth(Math.min(dayOfMonth, lastDay)));
            
            return t;
        }).toList();
    }

    @Override
    public List<Transaction> findAllTransactions() {
        return transactions;
    }
}