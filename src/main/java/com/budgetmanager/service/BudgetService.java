package com.budgetmanager.service;

import com.budgetmanager.dto.BudgetProgress;
import com.budgetmanager.entity.Budget;
import com.budgetmanager.entity.Transaction;
import com.budgetmanager.entity.User;
import com.budgetmanager.repository.BudgetRepository;
import com.budgetmanager.repository.TransactionRepository;
import com.budgetmanager.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;


    public BudgetService(
            BudgetRepository budgetRepository,
            UserRepository userRepository,
            TransactionRepository transactionRepository) {

        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }


    // =========================
    // SAVE BUDGET
    // =========================

    public Budget saveBudget(
            Budget budget,
            String email) {

        User user = getUserByEmail(email);

        budget.setUser(user);


        if (budget.getMonth() == null) {
            budget.setMonth(
                    LocalDate.now().getMonthValue()
            );
        }


        if (budget.getYear() == null) {
            budget.setYear(
                    LocalDate.now().getYear()
            );
        }


        Optional<Budget> existingBudget =
                budgetRepository
                        .findByUserAndCategoryAndMonthAndYear(
                                user,
                                budget.getCategory(),
                                budget.getMonth(),
                                budget.getYear()
                        );


        // Same category ka budget hai
        // to duplicate create nahi hoga
        if (existingBudget.isPresent()) {

            Budget existing =
                    existingBudget.get();

            existing.setAmount(
                    budget.getAmount()
            );

            return budgetRepository.save(existing);
        }


        return budgetRepository.save(budget);
    }


    // =========================
    // CURRENT MONTH BUDGETS
    // =========================

    public List<Budget> getCurrentMonthBudgets(
            String email) {

        User user = getUserByEmail(email);

        LocalDate today = LocalDate.now();


        return budgetRepository
                .findByUserAndMonthAndYear(
                        user,
                        today.getMonthValue(),
                        today.getYear()
                );
    }


    // =========================
    // BUDGET PROGRESS
    // =========================

    public List<BudgetProgress> getBudgetProgress(
            String email) {

        User user = getUserByEmail(email);

        LocalDate today = LocalDate.now();


        int month = today.getMonthValue();
        int year = today.getYear();


        // Current month ka first day
        LocalDate startDate =
                LocalDate.of(
                        year,
                        month,
                        1
                );


        // Current month ka last day
        LocalDate endDate =
                startDate.withDayOfMonth(
                        startDate.lengthOfMonth()
                );


        // Current month ke budgets
        List<Budget> budgets =
                budgetRepository
                        .findByUserAndMonthAndYear(
                                user,
                                month,
                                year
                        );


        List<BudgetProgress> progressList =
                new ArrayList<>();


        for (Budget budget : budgets) {


            // Same category ke expenses
            List<Transaction> expenses =
                    transactionRepository
                            .findByUserAndTypeAndCategoryAndTransactionDateBetween(
                                    user,
                                    "EXPENSE",
                                    budget.getCategory(),
                                    startDate,
                                    endDate
                            );


            // Total spent calculate
            BigDecimal spent =
                    expenses.stream()
                            .map(Transaction::getAmount)
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            BigDecimal budgetAmount =
                    budget.getAmount();


            double percentage = 0;


            // Divide by zero avoid karne ke liye
            if (budgetAmount != null &&
                    budgetAmount.compareTo(BigDecimal.ZERO) > 0) {

                percentage =
                        spent
                                .multiply(
                                        BigDecimal.valueOf(100)
                                )
                                .divide(
                                        budgetAmount,
                                        2,
                                        RoundingMode.HALF_UP
                                )
                                .doubleValue();
            }


            // Progress bar 100% se bahar na jaye
            double displayPercentage =
                    Math.min(
                            percentage,
                            100
                    );


            BudgetProgress progress =
                    new BudgetProgress(
                            budget.getCategory(),
                            budgetAmount,
                            spent,
                            displayPercentage
                    );


            progressList.add(progress);
        }


        return progressList;
    }


    // =========================
    // USER HELPER
    // =========================

    private User getUserByEmail(
            String email) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );
    }
}