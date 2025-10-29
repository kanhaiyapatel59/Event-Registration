package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.entity.Registration;
import com.smartevents.event_registration_system.repository.EventRepository;
import com.smartevents.event_registration_system.repository.RegistrationRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    // ✅ Show registration form for an event - CLEAN VERSION
    @GetMapping("/register/{eventId}")
    public String showRegistrationForm(@PathVariable Long eventId, Model model) {
        try {
            Optional<Event> event = eventRepository.findById(eventId);

            if (event.isPresent()) {
                Event currentEvent = event.get();

                // ✅ Check if event is in the past
                boolean isPastEvent = currentEvent.getEventDate().isBefore(java.time.LocalDateTime.now());

                // ✅ Calculate available spots
                long currentRegistrations = registrationRepository.countByEventAndStatus(currentEvent, "REGISTERED");
                int availableSpots = currentEvent.getMaxAttendees() - (int) currentRegistrations;

                Registration registration = new Registration();
                registration.setEvent(currentEvent);

                model.addAttribute("registration", registration);
                model.addAttribute("event", currentEvent);
                model.addAttribute("availableSpots", availableSpots);
                model.addAttribute("currentRegistrations", currentRegistrations);
                model.addAttribute("isPastEvent", isPastEvent);

                return "registrations/register";
            } else {
                return "redirect:/events/list";
            }
        } catch (Exception e) {
            return "redirect:/events/list";
        }
    }

    // ✅ Handle registration form submission - FIXED VERSION
    @PostMapping("/register")
    public String registerForEvent(@Valid @ModelAttribute Registration registration,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        
        System.out.println("=== DEBUG: Starting registration process ===");
        
        // Get the event from the registration
        Event event = registration.getEvent();
        System.out.println("Event ID: " + event.getId());
        System.out.println("Event name: " + event.getName());
        
        // First, validate the form
        if (result.hasErrors()) {
            System.out.println("Form has validation errors");
            model.addAttribute("event", event);
            return "registrations/register";
        }

        // Refresh the event from database to get current data
        Optional<Event> currentEventOpt = eventRepository.findById(event.getId());
        if (!currentEventOpt.isPresent()) {
            System.out.println("Event not found in database");
            model.addAttribute("error", "Event not found.");
            return "redirect:/events/list";
        }
        
        Event currentEvent = currentEventOpt.get();
        System.out.println("Current event max attendees: " + currentEvent.getMaxAttendees());

        // Check if already registered (FIRST check)
        boolean alreadyRegistered = registrationRepository.existsByEventAndEmail(currentEvent, registration.getEmail());
        System.out.println("Already registered: " + alreadyRegistered);
        
        if (alreadyRegistered) {
            model.addAttribute("error", "You are already registered for this event with this email.");
            model.addAttribute("event", currentEvent);
            
            // Recalculate available spots for the view
            long currentRegistrations = registrationRepository.countByEventAndStatus(currentEvent, "REGISTERED");
            int availableSpots = currentEvent.getMaxAttendees() - (int) currentRegistrations;
            model.addAttribute("availableSpots", availableSpots);
            
            return "registrations/register";
        }

        // Check if event is full (SECOND check)
        long currentRegistrations = registrationRepository.countByEventAndStatus(currentEvent, "REGISTERED");
        int availableSpots = currentEvent.getMaxAttendees() - (int) currentRegistrations;
        
        System.out.println("Current registrations: " + currentRegistrations);
        System.out.println("Available spots: " + availableSpots);
        System.out.println("Max attendees: " + currentEvent.getMaxAttendees());

        if (availableSpots <= 0) {
            System.out.println("Event is full");
            model.addAttribute("error", "Sorry, this event is fully booked.");
            model.addAttribute("event", currentEvent);
            model.addAttribute("availableSpots", availableSpots);
            return "registrations/register";
        }

        // All checks passed - save registration
        System.out.println("All checks passed - saving registration");
        registration.setEvent(currentEvent); // Use the fresh event from database
        registrationRepository.save(registration);
        
        redirectAttributes.addFlashAttribute("success", "Successfully registered for " + currentEvent.getName() + "!");
        return "redirect:/events/list";
    }

    // ✅ View all registrations (admin view)
    @GetMapping("/all")
    public String viewAllRegistrations(Model model) {
        try {
            List<Registration> registrations = registrationRepository.findAllWithEvent();
            model.addAttribute("registrations", registrations);
            return "registrations/all";
        } catch (Exception e) {
            List<Registration> registrations = registrationRepository.findAllByOrderByRegistrationDateDesc();
            model.addAttribute("registrations", registrations);
            return "registrations/all";
        }
    }

    // ✅ View registrations for a specific event
    @GetMapping("/event/{eventId}")
    public String viewEventRegistrations(@PathVariable Long eventId, Model model) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            List<Registration> registrations = registrationRepository.findByEventOrderByRegistrationDateDesc(event.get());
            model.addAttribute("event", event.get());
            model.addAttribute("registrations", registrations);
            return "registrations/event-registrations";
        } else {
            return "redirect:/events/list";
        }
    }

    // ✅ Cancel a registration
    @GetMapping("/cancel/{registrationId}")
    public String cancelRegistration(@PathVariable Long registrationId, RedirectAttributes redirectAttributes) {
        Optional<Registration> registration = registrationRepository.findById(registrationId);
        if (registration.isPresent()) {
            Registration reg = registration.get();
            reg.setStatus("CANCELLED");
            registrationRepository.save(reg);
            redirectAttributes.addFlashAttribute("success", "Registration cancelled successfully.");
        }
        return "redirect:/registrations/all";
    }

    // ✅ Delete a registration
    @GetMapping("/delete/{registrationId}")
    public String deleteRegistration(@PathVariable Long registrationId, RedirectAttributes redirectAttributes) {
        registrationRepository.deleteById(registrationId);
        redirectAttributes.addFlashAttribute("success", "Registration deleted successfully.");
        return "redirect:/registrations/all";
    }
}
