package com.budgetmanager.service;

import com.budgetmanager.entity.Goal;
import com.budgetmanager.entity.User;
import com.budgetmanager.repository.GoalRepository;
import com.budgetmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;


    public GoalService(GoalRepository goalRepository,
                       UserRepository userRepository) {

        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }


    // Logged-in user find karne ke liye
    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }


    // User ke saare goals
    public List<Goal> getGoals(String email) {

        User user = getUserByEmail(email);

        return goalRepository
                .findByUserOrderByCreatedAtDesc(user);
    }


    // New goal create
    public void createGoal(Goal goal, String email) {

        User user = getUserByEmail(email);

        goal.setUser(user);

        if (goal.getSavedAmount() == null) {
            goal.setSavedAmount(BigDecimal.ZERO);
        }

        goalRepository.save(goal);
    }


    // Particular goal find
    public Goal getGoal(Long id, String email) {

        User user = getUserByEmail(email);

        return goalRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Goal not found"));
    }


    // Goal delete
    public void deleteGoal(Long id, String email) {

        Goal goal = getGoal(id, email);

        goalRepository.delete(goal);
    }

    public void addSavings(Long id, BigDecimal amount, String email) {

        // Logged-in user ka goal nikalo
        Goal goal = getGoal(id, email);

        // Invalid amount ko reject karo
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Savings amount must be greater than zero"
            );
        }

        // Current saved amount
        BigDecimal currentSaved = goal.getSavedAmount();

        if (currentSaved == null) {
            currentSaved = BigDecimal.ZERO;
        }

        // New saved amount
        BigDecimal newSaved = currentSaved.add(amount);

        // Target se zyada save na hone do
        if (newSaved.compareTo(goal.getTargetAmount()) > 0) {
            newSaved = goal.getTargetAmount();
        }

        goal.setSavedAmount(newSaved);

        goalRepository.save(goal);
    }
}