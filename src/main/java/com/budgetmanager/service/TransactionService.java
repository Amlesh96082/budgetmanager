package com.budgetmanager.service;

import com.budgetmanager.entity.Transaction;
import com.budgetmanager.entity.User;
import com.budgetmanager.repository.TransactionRepository;
import com.budgetmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import com.budgetmanager.dto.MonthlySummary;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository) {

        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }


    // Transaction save karega
    public Transaction saveTransaction(
            Transaction transaction,
            String email) {

        User user = getUserByEmail(email);

        transaction.setUser(user);

        return transactionRepository.save(transaction);
    }


    // Logged-in user ke saare transactions
    public List<Transaction> getTransactions(String email) {

        User user = getUserByEmail(email);

        return transactionRepository
                .findByUserOrderByTransactionDateDesc(user);
    }


    // Total Income
    public BigDecimal getTotalIncome(String email) {

        return getTransactions(email)
                .stream()
                .filter(transaction ->
                        "INCOME".equalsIgnoreCase(transaction.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Total Expense
    public BigDecimal getTotalExpense(String email) {

        return getTransactions(email)
                .stream()
                .filter(transaction ->
                        "EXPENSE".equalsIgnoreCase(transaction.getType()))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Total Balance = Income - Expense
    public BigDecimal getBalance(String email) {

        BigDecimal income = getTotalIncome(email);

        BigDecimal expense = getTotalExpense(email);

        return income.subtract(expense);
    }


    // User find karne ke liye helper method
    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    // Transaction ko ID se find karega
    public Transaction getTransactionById(Long id, String email) {

        User user = getUserByEmail(email);

        return transactionRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Transaction not found"));
    }


    // Existing transaction update karega
    public Transaction updateTransaction(
            Long id,
            Transaction updatedTransaction,
            String email) {

        Transaction existingTransaction =
                getTransactionById(id, email);

        existingTransaction.setTitle(
                updatedTransaction.getTitle());

        existingTransaction.setAmount(
                updatedTransaction.getAmount());

        existingTransaction.setType(
                updatedTransaction.getType());

        existingTransaction.setCategory(
                updatedTransaction.getCategory());

        existingTransaction.setTransactionDate(
                updatedTransaction.getTransactionDate());

        existingTransaction.setDescription(
                updatedTransaction.getDescription());

        return transactionRepository.save(existingTransaction);
    }


    // Transaction delete karega
    public void deleteTransaction(Long id, String email) {

        Transaction transaction =
                getTransactionById(id, email);

        transactionRepository.delete(transaction);
    }
    public List<MonthlySummary> getLastSixMonthsSummary(String email) {

        User user = getUserByEmail(email);

        List<Transaction> transactions =
                transactionRepository
                        .findByUserOrderByTransactionDateDesc(user);

        List<MonthlySummary> monthlySummaries =
                new ArrayList<>();

        LocalDate currentMonth =
                LocalDate.now().withDayOfMonth(1);


        // =====================================
        // LAST 6 MONTHS DATA
        // =====================================

        for (int i = 5; i >= 0; i--) {

            LocalDate targetMonth =
                    currentMonth.minusMonths(i);

            int monthValue =
                    targetMonth.getMonthValue();

            int year =
                    targetMonth.getYear();


            // -------- INCOME --------

            BigDecimal income =
                    transactions.stream()

                            .filter(t ->
                                    t.getTransactionDate() != null)

                            .filter(t ->
                                    t.getTransactionDate()
                                            .getMonthValue()
                                            == monthValue)

                            .filter(t ->
                                    t.getTransactionDate()
                                            .getYear()
                                            == year)

                            .filter(t ->
                                    "INCOME".equalsIgnoreCase(
                                            t.getType()
                                    ))

                            .map(Transaction::getAmount)

                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            // -------- EXPENSE --------

            BigDecimal expense =
                    transactions.stream()

                            .filter(t ->
                                    t.getTransactionDate() != null)

                            .filter(t ->
                                    t.getTransactionDate()
                                            .getMonthValue()
                                            == monthValue)

                            .filter(t ->
                                    t.getTransactionDate()
                                            .getYear()
                                            == year)

                            .filter(t ->
                                    "EXPENSE".equalsIgnoreCase(
                                            t.getType()
                                    ))

                            .map(Transaction::getAmount)

                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            );


            String monthName =
                    targetMonth
                            .getMonth()
                            .getDisplayName(
                                    TextStyle.SHORT,
                                    Locale.ENGLISH
                            );


            monthlySummaries.add(
                    new MonthlySummary(
                            monthName,
                            income,
                            expense
                    )
            );
        }


        // =====================================
        // FIND MAXIMUM AMOUNT
        // =====================================

        BigDecimal maxAmount = BigDecimal.ZERO;

        for (MonthlySummary summary : monthlySummaries) {

            if (summary.getIncome()
                    .compareTo(maxAmount) > 0) {

                maxAmount = summary.getIncome();
            }


            if (summary.getExpense()
                    .compareTo(maxAmount) > 0) {

                maxAmount = summary.getExpense();
            }
        }


        // =====================================
        // CALCULATE BAR HEIGHT
        // =====================================

        if (maxAmount.compareTo(BigDecimal.ZERO) > 0) {

            for (MonthlySummary summary : monthlySummaries) {

                double incomeHeight =
                        summary.getIncome()
                                .doubleValue()
                                / maxAmount.doubleValue()
                                * 100;


                double expenseHeight =
                        summary.getExpense()
                                .doubleValue()
                                / maxAmount.doubleValue()
                                * 100;


                summary.setIncomeHeight(incomeHeight);

                summary.setExpenseHeight(expenseHeight);
            }
        }


        return monthlySummaries;
    }
}