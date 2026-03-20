package com.reward;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for RewardController.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RewardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnRewardSummaryForSingleCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/summary/101")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.customerName").value("Vidya Baligar"))
                .andExpect(jsonPath("$.monthlyRewards").isArray())
                .andExpect(jsonPath("$.monthlyRewards.length()").value(3))
                .andExpect(jsonPath("$.monthlyRewards[0].points").value(115))
                .andExpect(jsonPath("$.monthlyRewards[1].points").value(250))
                .andExpect(jsonPath("$.monthlyRewards[2].points").value(110))
                .andExpect(jsonPath("$.totalPoints").value(475));
    }

    @Test
    void shouldReturn404ForUnknownCustomer() throws Exception {
        mockMvc.perform(get("/api/rewards/summary/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found for id: 999"));
    }
}