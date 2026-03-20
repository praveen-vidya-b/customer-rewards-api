package com.reward.controller;

import com.reward.dto.CustomerRewardSummaryDto;
import com.reward.service.RewardService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for reward-related endpoints.
 */
@RestController
@RequestMapping("/api/rewards")
public class RewardController {

    private final RewardService rewardService;

    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    /**
     * Returns reward summary for a single customer.
     */
    @GetMapping("/summary/{customerId}")
    public CustomerRewardSummaryDto getRewardSummaryByCustomerId(@PathVariable Long customerId) {
        return rewardService.getRewardSummaryByCustomerId(customerId);
    }
}