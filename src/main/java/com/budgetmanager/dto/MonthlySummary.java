package com.budgetmanager.dto;

import java.math.BigDecimal;

public class MonthlySummary {

    private String month;
    private BigDecimal income;
    private BigDecimal expense;

    private double incomeHeight;
    private double expenseHeight;


    public MonthlySummary(
            String month,
            BigDecimal income,
            BigDecimal expense) {

        this.month = month;
        this.income = income;
        this.expense = expense;
    }


    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }


    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }


    public BigDecimal getExpense() {
        return expense;
    }

    public void setExpense(BigDecimal expense) {
        this.expense = expense;
    }


    public double getIncomeHeight() {
        return incomeHeight;
    }

    public void setIncomeHeight(double incomeHeight) {
        this.incomeHeight = incomeHeight;
    }


    public double getExpenseHeight() {
        return expenseHeight;
    }

    public void setExpenseHeight(double expenseHeight) {
        this.expenseHeight = expenseHeight;
    }
}