package com.budgetmanager.dto;

import java.math.BigDecimal;

public class BudgetProgress {

    private String category;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private double percentage;

    public BudgetProgress(
            String category,
            BigDecimal budgetAmount,
            BigDecimal spentAmount,
            double percentage) {

        this.category = category;
        this.budgetAmount = budgetAmount;
        this.spentAmount = spentAmount;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public double getPercentage() {
        return percentage;
    }
}