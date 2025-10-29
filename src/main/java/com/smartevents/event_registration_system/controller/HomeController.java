package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.repository.EventRepository;
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

    @GetMapping("/")
    public String home(Model model) {
        // Get upcoming events
        List<Event> upcomingEvents = eventRepository.findByEventDateAfterOrderByEventDateAsc(LocalDateTime.now());
        
        model.addAttribute("upcomingEvents", upcomingEvents);
        return "home";
    }
}