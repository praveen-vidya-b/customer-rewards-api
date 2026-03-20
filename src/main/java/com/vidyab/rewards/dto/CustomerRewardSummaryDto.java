package com.vidyab.rewards.dto;

import java.util.List;

/**
 * Reward summary for a customer across the last three months.
 */
public class CustomerRewardSummaryDto {

    private Long customerId;
    private String customerName;
    private List<MonthlyRewardDto> monthlyRewards;
    private long totalPoints;

    public CustomerRewardSummaryDto() {
    }

    public CustomerRewardSummaryDto(Long customerId, String customerName,
                                    List<MonthlyRewardDto> monthlyRewards, long totalPoints) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.monthlyRewards = monthlyRewards;
        this.totalPoints = totalPoints;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<MonthlyRewardDto> getMonthlyRewards() {
        return monthlyRewards;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setMonthlyRewards(List<MonthlyRewardDto> monthlyRewards) {
        this.monthlyRewards = monthlyRewards;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }
}