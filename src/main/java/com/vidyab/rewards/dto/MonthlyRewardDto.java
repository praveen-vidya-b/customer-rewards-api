package com.vidyab.rewards.dto;

/**
 * Holds reward points for a specific month.
 */
public class MonthlyRewardDto {

    private String month;
    private long points;

    public MonthlyRewardDto() {
    }

    public MonthlyRewardDto(String month, long points) {
        this.month = month;
        this.points = points;
    }

    public String getMonth() {
        return month;
    }

    public long getPoints() {
        return points;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setPoints(long points) {
        this.points = points;
    }
}