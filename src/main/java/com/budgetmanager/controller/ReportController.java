package com.budgetmanager.controller;

import com.budgetmanager.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }


    // =========================
    // REPORTS PAGE
    // =========================

    @GetMapping("/reports")
    public String showReports(
            Principal principal,
            Model model) {

        String email = principal.getName();


        // Total Income
        model.addAttribute(
                "totalIncome",
                reportService.getTotalIncome(email)
        );


        // Total Expenses
        model.addAttribute(
                "totalExpense",
                reportService.getTotalExpense(email)
        );


        // Net Savings
        model.addAttribute(
                "netSavings",
                reportService.getNetSavings(email)
        );


        // Category-wise expenses
        model.addAttribute(
                "categoryExpenses",
                reportService.getExpenseByCategory(email)
        );

        model.addAttribute(
                "monthlyReport",
                reportService.getMonthlyReport(email)
        );

        return "reports";
    }
}