package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.repository.EventRepository;
import com.smartevents.event_registration_system.repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RegistrationRepository registrationRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Get upcoming events (next 30 days)
        LocalDateTime thirtyDaysFromNow = LocalDateTime.now().plusDays(30);
        List<Event> upcomingEvents = eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());
        
        // Calculate statistics
        long totalRegistrations = registrationRepository.count();
        long uniqueAttendees = registrationRepository.findAll().stream()
                .map(reg -> reg.getEmail().toLowerCase())
                .distinct()
                .count();
        
        model.addAttribute("upcomingEvents", upcomingEvents);
        model.addAttribute("totalRegistrations", totalRegistrations);
        model.addAttribute("uniqueAttendees", uniqueAttendees);
        
        return "home";
    }
}