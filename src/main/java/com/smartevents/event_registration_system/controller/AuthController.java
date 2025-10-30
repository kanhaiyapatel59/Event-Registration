package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.User;
import com.smartevents.event_registration_system.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (model.containsAttribute("success")) {
            model.addAttribute("success", model.getAttribute("success"));
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/process-register")
    public String processRegistration(
            @Valid @ModelAttribute User user,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        logger.info("Starting registration process for user: {}", user.getUsername());

        //  Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            logger.warn("Username already exists: {}", user.getUsername());
            result.rejectValue("username", "error.user", "Username already exists");
        }

        // ✅Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            logger.warn("Email already exists: {}", user.getEmail());
            result.rejectValue("email", "error.user", "Email already registered");
        }

        // Check if contact number already exists
        if (userRepository.existsByContactNumber(user.getContactNumber())) {
            logger.warn("Contact number already exists: {}", user.getContactNumber());
            result.rejectValue("contactNumber", "error.user", "Contact number already registered");
        }

        // If there are validation errors, return to form
        if (result.hasErrors()) {
            logger.error("Validation errors found: {}", result.getAllErrors());
            return "auth/register";
        }

        try {
            logger.info("Setting up user data for: {}", user.getUsername());

            // Encode password and set role
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole("USER");

            //  Handle name fields - ensure firstName is set
            if (user.getFullName() != null && !user.getFullName().isEmpty()) {
                String[] nameParts = user.getFullName().split(" ", 2);
                user.setFirstName(nameParts[0]); // First name
                if (nameParts.length > 1) {
                    user.setLastName(nameParts[1]); // Last name
                } else {
                    user.setLastName("");
                }
                logger.info("Name processed - First: {}, Last: {}, Full: {}", 
                        user.getFirstName(), user.getLastName(), user.getFullName());
            } else {
                // Default values if no full name is given
                user.setFirstName("User");
                user.setFullName("User");
                user.setLastName("");
            }

            // Ensure contact number is not null
            if (user.getContactNumber() == null || user.getContactNumber().isEmpty()) {
                user.setContactNumber("000-000-0000");
            }

            // Default location values
            user.setLatitude(0.0);
            user.setLongitude(0.0);

            // Save user
            logger.info("Attempting to save user: {}", user.getUsername());
            userRepository.save(user);
            logger.info("User saved successfully: {}", user.getUsername());

            // Redirect to login with success message
            redirectAttributes.addFlashAttribute("success", 
                    "Registration successful! Please login with your credentials.");
            return "redirect:/login";

        } catch (Exception e) {
            logger.error("Registration failed for user: {}", user.getUsername(), e);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "auth/register";
        }
    }
}
