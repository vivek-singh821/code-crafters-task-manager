
package com.example.taskmanager;

import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        name = name.trim();
        email = email.trim().toLowerCase();

        if (name.isBlank() || password.length() < 8) {
            model.addAttribute("error",
                    "Enter your name and a password of at least 8 characters.");
            return "register";
        }

        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error",
                    "This email is already registered.");
            return "register";
        }

        userRepository.save(new AppUser(
                name, email, passwordEncoder.encode(password)));

        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String registered,
            Model model) {

        if (registered != null) {
            model.addAttribute("message",
                    "Registration successful! Please log in.");
        }

        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        AppUser user = userRepository
                .findByEmail(email.trim().toLowerCase())
                .orElse(null);

        if (user != null &&
                passwordEncoder.matches(password, user.getPassword())) {

            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());

            return "redirect:/tasks";
        }

        model.addAttribute("error", "Invalid email or password.");
        return "login";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
