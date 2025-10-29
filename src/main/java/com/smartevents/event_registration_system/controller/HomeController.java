package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        // Get upcoming events
        List<Event> upcomingEvents = eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());
        
        model.addAttribute("upcomingEvents", upcomingEvents);
        
        // Add user info if logged in
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("currentUser", authentication.getName());
            model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        }
        
        return "home";
    }
}