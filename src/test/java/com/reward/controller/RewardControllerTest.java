package com.reward.controller;

import com.reward.dto.CustomerRewardSummaryDto;
import com.reward.dto.MonthlyRewardDto;
import com.reward.exception.ResourceNotFoundException;
import com.reward.exception.InvalidTransactionException;
import com.reward.service.RewardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RewardController.class)
class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RewardService rewardService;

    @Test
    void shouldReturnRewardSummaryForValidCustomerId() throws Exception {
        Long customerId = 101L;
        CustomerRewardSummaryDto summary = new CustomerRewardSummaryDto(
                customerId,
                "Vidya Baligar",
                List.of(new MonthlyRewardDto("January 2026", 115)),
                115
        );

        when(rewardService.getRewardSummaryByCustomerId(customerId)).thenReturn(summary);

        mockMvc.perform(get("/api/rewards/summary/{customerId}", customerId)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.customerId").value(customerId))
                .andExpect(jsonPath("$.customerName").value("Vidya Baligar"))
                .andExpect(jsonPath("$.totalPoints").value(115))
                .andExpect(jsonPath("$.monthlyRewards[0].month").value("January 2026"))
                .andExpect(jsonPath("$.monthlyRewards[0].points").value(115));
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {
        Long customerId = 999L;
        when(rewardService.getRewardSummaryByCustomerId(customerId))
                .thenThrow(new ResourceNotFoundException("Customer not found for id: " + customerId));

        mockMvc.perform(get("/api/rewards/summary/{customerId}", customerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer not found for id: 999"));
    }

    @Test
    void shouldReturn400WhenTransactionDataIsInvalid() throws Exception {
        Long customerId = 102L;
        when(rewardService.getRewardSummaryByCustomerId(customerId))
                .thenThrow(new InvalidTransactionException("Transaction list cannot be null."));

        mockMvc.perform(get("/api/rewards/summary/{customerId}", customerId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Transaction list cannot be null."));
    }
}
