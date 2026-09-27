package com.budgetmanager.controller;

import com.budgetmanager.entity.User;
import com.budgetmanager.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SettingsController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(UserRepository userRepository,
                              PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/settings")
    public String showSettings(
            Principal principal,
            Model model) {

        String email = principal.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        model.addAttribute("user", user);

        return "settings";
    }

    @GetMapping("/settings/change-password")
    public String showChangePasswordPage() {
        return "change-password";
    }
    @PostMapping("/settings/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));


        // Current password check
        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Current password is incorrect."
            );

            return "redirect:/settings/change-password";
        }


        // New password confirmation check
        if (!newPassword.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "New password and confirm password do not match."
            );

            return "redirect:/settings/change-password";
        }


        // Minimum password length
        if (newPassword.length() < 6) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Password must be at least 6 characters."
            );

            return "redirect:/settings/change-password";
        }


        // Encode and save new password
        user.setPassword(
                passwordEncoder.encode(newPassword)
        );

        userRepository.save(user);


        redirectAttributes.addFlashAttribute(
                "success",
                "Password changed successfully."
        );

        return "redirect:/settings/change-password";
    }
    @PostMapping("/settings/update-profile")
    public String updateProfile(
            @RequestParam String name,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String email = principal.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (name == null || name.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute(
                    "profileError",
                    "Name cannot be empty."
            );

            return "redirect:/settings";
        }

        user.setName(name.trim());

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "profileSuccess",
                "Profile updated successfully."
        );

        return "redirect:/settings";
    }
}