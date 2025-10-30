package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.entity.Registration;
import com.smartevents.event_registration_system.entity.Feedback;
import com.smartevents.event_registration_system.repository.EventRepository;
import com.smartevents.event_registration_system.repository.RegistrationRepository;
import com.smartevents.event_registration_system.repository.FeedbackRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventRepository eventRepository;

    // Registration repository for fetching event registrations
    @Autowired
    private RegistrationRepository registrationRepository;

    //  Feedback repository for fetching event feedbacks
    @Autowired
    private FeedbackRepository feedbackRepository;

    // Display all events
    @GetMapping("/list")
    public String listEvents(Model model) {
        List<Event> events = eventRepository.findAllByOrderByEventDateAsc();
        model.addAttribute("events", events);
        return "events/list";
    }

    // Show form to create new event
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        return "events/create";
    }

    // Handle form submission for new event
    @PostMapping("/create")
    public String createEvent(@Valid @ModelAttribute Event event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "events/create";
        }
        eventRepository.save(event);
        return "redirect:/events/list";
    }

    // Show form to edit event
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            return "events/edit";
        } else {
            return "redirect:/events/list";
        }
    }

    // Handle form submission for editing event
    @PostMapping("/update/{id}")
    public String updateEvent(@PathVariable Long id, @Valid @ModelAttribute Event event, BindingResult result) {
        if (result.hasErrors()) {
            return "events/edit";
        }
        event.setId(id);
        eventRepository.save(event);
        return "redirect:/events/list";
    }

    // Delete event
    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return "redirect:/events/list";
    }

    //  View event details and registrations
    @GetMapping("/view/{id}")
    public String viewEvent(@PathVariable Long id, Model model) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            List<Registration> registrations =
                    registrationRepository.findByEventOrderByRegistrationDateDesc(event.get());
            model.addAttribute("event", event.get());
            model.addAttribute("registrations", registrations);
            model.addAttribute("registrationCount", event.get().getRegistrationCount());
            model.addAttribute("availableSpots",
                    event.get().getMaxAttendees() - event.get().getRegistrationCount());
            return "events/view";
        } else {
            return "redirect:/events/list";
        }
    }

    //  New: View event feedback and ratings
    @GetMapping("/feedback/{id}")
    public String viewEventFeedback(@PathVariable Long id, Model model) {
        Optional<Event> event = eventRepository.findById(id);
        if (event.isPresent()) {
            List<Feedback> feedbacks =
                    feedbackRepository.findByEventAndApprovedTrueOrderBySubmittedAtDesc(event.get());
            model.addAttribute("event", event.get());
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("averageRating", event.get().getAverageRating());
            return "events/feedback";
        } else {
            return "redirect:/events/list";
        }
    }
}
