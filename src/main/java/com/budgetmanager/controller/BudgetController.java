package com.budgetmanager.controller;

import com.budgetmanager.entity.Budget;
import com.budgetmanager.service.BudgetService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }


    // Budget page open karega
    @GetMapping
    public String showBudgetPage(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        model.addAttribute(
                "budgets",
                budgetService.getCurrentMonthBudgets(email)
        );

        Budget budget = new Budget();

        budget.setMonth(LocalDate.now().getMonthValue());
        budget.setYear(LocalDate.now().getYear());

        model.addAttribute("budget", budget);

        model.addAttribute(
                "currentMonth",
                LocalDate.now().getMonth().toString()
        );

        model.addAttribute(
                "currentYear",
                LocalDate.now().getYear()
        );

        return "budget";
    }


    // Budget database me save karega
    @PostMapping("/save")
    public String saveBudget(
            @ModelAttribute Budget budget,
            Authentication authentication) {

        String email = authentication.getName();

        budgetService.saveBudget(budget, email);

        return "redirect:/budgets";
    }
}