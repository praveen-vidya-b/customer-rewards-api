package com.vidyab.rewards.service;

import com.vidyab.rewards.dto.CustomerRewardSummaryDto;
import com.vidyab.rewards.exception.InvalidTransactionException;
import com.vidyab.rewards.exception.ResourceNotFoundException;

/**
 * Service for reward calculation and summary generation.
 */
public interface RewardService {

    /**
     * Builds a three-month reward summary for the given customer.
     *
     * @param customerId ID of the customer whose rewards should be calculated
     * @return reward summary including monthly breakdown and total points
     * @throws ResourceNotFoundException if the customer or their recent transactions are not found
     * @throws InvalidTransactionException if transaction data is invalid
     */
    CustomerRewardSummaryDto getRewardSummaryByCustomerId(Long customerId);

    /**
     * Calculates reward points for a single transaction amount using the configured formula.
     * <p>
     * Formula: 1 point for every dollar between $50 and $100 (exclusive of the first $50),
     * and 2 points for every dollar above $100. Amounts are floored to whole dollars.
     *
     * @param amount transaction amount in dollars
     * @return points earned for the given amount
     * @throws InvalidTransactionException if the amount is negative
     */
    long calculateRewardPoints(double amount);
}