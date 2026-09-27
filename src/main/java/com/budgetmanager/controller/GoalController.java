package com.budgetmanager.controller;

import com.budgetmanager.entity.Goal;
import com.budgetmanager.service.GoalService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }


    // =========================
    // SHOW ALL GOALS
    // =========================

    @GetMapping
    public String showGoals(
            Principal principal,
            Model model) {

        String email = principal.getName();

        model.addAttribute(
                "goals",
                goalService.getGoals(email)
        );

        return "goals";
    }


    // =========================
    // SHOW ADD GOAL PAGE
    // =========================

    @GetMapping("/add")
    public String showAddGoalPage(Model model) {

        model.addAttribute("goal", new Goal());

        return "add-goal";
    }


    // =========================
    // SAVE NEW GOAL
    // =========================

    @PostMapping("/add")
    public String createGoal(
            @ModelAttribute Goal goal,
            Principal principal) {

        String email = principal.getName();

        goalService.createGoal(goal, email);

        return "redirect:/goals";
    }


    // =========================
    // DELETE GOAL
    // =========================

    @PostMapping("/{id}/delete")
    public String deleteGoal(
            @PathVariable Long id,
            Principal principal) {

        String email = principal.getName();

        goalService.deleteGoal(id, email);

        return "redirect:/goals";
    }
    // =========================
// SHOW ADD SAVINGS PAGE
// =========================

    @GetMapping("/{id}/add-savings")
    public String showAddSavingsPage(
            @PathVariable Long id,
            Principal principal,
            Model model) {

        String email = principal.getName();

        Goal goal = goalService.getGoal(id, email);

        model.addAttribute("goal", goal);

        return "add-savings";
    }


// =========================
// ADD SAVINGS
// =========================

    @PostMapping("/{id}/add-savings")
    public String addSavings(
            @PathVariable Long id,
            @RequestParam("amount") java.math.BigDecimal amount,
            Principal principal) {
            String email = principal.getName();

            goalService.addSavings(id, amount, email);

        return "redirect:/goals";
    }
}