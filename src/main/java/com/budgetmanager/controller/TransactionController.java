package com.budgetmanager.controller;

import com.budgetmanager.entity.Transaction;
import com.budgetmanager.service.TransactionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // Add Income/Expense form open karega
    @GetMapping("/add")
    public String showAddTransactionForm(
            @RequestParam(defaultValue = "EXPENSE") String type,
            Model model) {

        Transaction transaction = new Transaction();
        transaction.setType(type.toUpperCase());

        model.addAttribute("transaction", transaction);

        return "add-transaction";
    }


    // Form submit hone par database me save karega
    @PostMapping("/add")
    public String saveTransaction(
            @ModelAttribute Transaction transaction,
            Authentication authentication) {

        String email = authentication.getName();

        transactionService.saveTransaction(transaction, email);

        return "redirect:/dashboard";
    }
    @GetMapping
    public String showTransactions(
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        model.addAttribute(
                "transactions",
                transactionService.getTransactions(email)
        );

        return "transactions";
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        String email = authentication.getName();

        Transaction transaction =
                transactionService.getTransactionById(id, email);

        model.addAttribute(
                "transaction",
                transaction);

        return "edit-transaction";
    }


    @PostMapping("/edit/{id}")
    public String updateTransaction(
            @PathVariable Long id,
            @ModelAttribute Transaction transaction,
            Authentication authentication) {

        String email = authentication.getName();

        transactionService.updateTransaction(
                id,
                transaction,
                email);

        return "redirect:/transactions";
    }
    @PostMapping("/delete/{id}")
    public String deleteTransaction(
            @PathVariable Long id,
            Authentication authentication) {

        String email = authentication.getName();

        transactionService.deleteTransaction(
                id,
                email);

        return "redirect:/transactions";
    }
}