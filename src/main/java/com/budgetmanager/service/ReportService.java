package com.budgetmanager.service;

import com.budgetmanager.entity.Transaction;
import com.budgetmanager.entity.User;
import com.budgetmanager.repository.TransactionRepository;
import com.budgetmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ReportService(TransactionRepository transactionRepository,
                         UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }


    // =========================
    // GET USER
    // =========================

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }


    // =========================
    // GET ALL TRANSACTIONS
    // =========================

    public List<Transaction> getTransactions(String email) {

        User user = getUserByEmail(email);

        return transactionRepository
                .findByUserOrderByTransactionDateDesc(user);
    }


    // =========================
    // TOTAL INCOME
    // =========================

    public BigDecimal getTotalIncome(String email) {

        return getTransactions(email)
                .stream()

                .filter(transaction ->
                        "INCOME".equalsIgnoreCase(
                                transaction.getType()))

                .map(Transaction::getAmount)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }


    // =========================
    // TOTAL EXPENSE
    // =========================

    public BigDecimal getTotalExpense(String email) {

        return getTransactions(email)
                .stream()

                .filter(transaction ->
                        "EXPENSE".equalsIgnoreCase(
                                transaction.getType()))

                .map(Transaction::getAmount)

                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }


    // =========================
    // NET SAVINGS
    // =========================

    public BigDecimal getNetSavings(String email) {

        BigDecimal income =
                getTotalIncome(email);

        BigDecimal expense =
                getTotalExpense(email);

        return income.subtract(expense);
    }


    // =========================
    // CATEGORY-WISE EXPENSE
    // =========================

    public Map<String, BigDecimal>
    getExpenseByCategory(String email) {

        List<Transaction> transactions =
                getTransactions(email);

        Map<String, BigDecimal> categoryExpenses =
                new LinkedHashMap<>();


        for (Transaction transaction : transactions) {

            if ("EXPENSE".equalsIgnoreCase(
                    transaction.getType())) {

                String category =
                        transaction.getCategory();

                BigDecimal amount =
                        transaction.getAmount();


                categoryExpenses.merge(
                        category,
                        amount,
                        BigDecimal::add
                );
            }
        }

        return categoryExpenses;
    }
    // =========================
// MONTHLY INCOME & EXPENSE
// =========================

    public Map<String, BigDecimal[]> getMonthlyReport(String email) {

        List<Transaction> transactions = getTransactions(email);

        Map<String, BigDecimal[]> monthlyReport =
                new LinkedHashMap<>();


        for (Transaction transaction : transactions) {

            if (transaction.getTransactionDate() == null) {
                continue;
            }


            String month = transaction
                    .getTransactionDate()
                    .getMonth()
                    .toString();


            monthlyReport.putIfAbsent(
                    month,
                    new BigDecimal[]{
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    }
            );


            BigDecimal[] values =
                    monthlyReport.get(month);


            // Income
            if ("INCOME".equalsIgnoreCase(
                    transaction.getType())) {

                values[0] =
                        values[0].add(
                                transaction.getAmount()
                        );
            }


            // Expense
            else if ("EXPENSE".equalsIgnoreCase(
                    transaction.getType())) {

                values[1] =
                        values[1].add(
                                transaction.getAmount()
                        );
            }
        }


        return monthlyReport;
    }
}