package com.budgetmanager.controller;

import com.budgetmanager.entity.Transaction;
import com.budgetmanager.service.BudgetService;
import com.budgetmanager.service.TransactionService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.budgetmanager.service.BudgetService;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    private final TransactionService transactionService;
    private final BudgetService budgetService;

    public DashboardController(
            TransactionService transactionService,
            BudgetService budgetService) {

        this.transactionService = transactionService;
        this.budgetService = budgetService;
    }


    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();


        // Get totals

        BigDecimal totalIncome =
                transactionService.getTotalIncome(email);

        BigDecimal totalExpense =
                transactionService.getTotalExpense(email);

        BigDecimal balance =
                transactionService.getBalance(email);


        // Get transactions

        List<Transaction> transactions =
                transactionService.getTransactions(email);


        // Send data to HTML

        model.addAttribute("email", email);

        model.addAttribute(
                "totalIncome",
                totalIncome
        );

        model.addAttribute(
                "totalExpense",
                totalExpense
        );

        model.addAttribute(
                "balance",
                balance
        );

        model.addAttribute(
                "transactions",
                transactions
        );
        model.addAttribute(
                "budgetProgress",
                budgetService.getBudgetProgress(email)
        );

        model.addAttribute(
                "monthlySummary",
                transactionService.getLastSixMonthsSummary(email)
        );

        return "dashboard";
    }
}