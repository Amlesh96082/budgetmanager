package com.budgetmanager.repository;

import com.budgetmanager.entity.Transaction;
import com.budgetmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    // Logged-in user ke saare transactions
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);


    // ID aur User ke basis par transaction find karega
    Optional<Transaction> findByIdAndUser(
            Long id,
            User user
    );


    // Category ke current month expenses find karne ke liye
    List<Transaction> findByUserAndTypeAndCategoryAndTransactionDateBetween(
            User user,
            String type,
            String category,
            LocalDate startDate,
            LocalDate endDate
    );
}