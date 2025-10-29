package com.smartevents.event_registration_system.controller;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.entity.Feedback;
import com.smartevents.event_registration_system.repository.EventRepository;
import com.smartevents.event_registration_system.repository.FeedbackRepository;
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
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private EventRepository eventRepository;

    // Show feedback form for an event
    @GetMapping("/submit/{eventId}")
    public String showFeedbackForm(@PathVariable Long eventId, Model model) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            Feedback feedback = new Feedback();
            feedback.setEvent(event.get());
            model.addAttribute("feedback", feedback);
            model.addAttribute("event", event.get());
            return "feedback/submit";
        } else {
            return "redirect:/events/list";
        }
    }

    // Handle feedback form submission
    @PostMapping("/submit")
    public String submitFeedback(@Valid @ModelAttribute Feedback feedback,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("event", feedback.getEvent());
            return "feedback/submit";
        }

        // Check if already submitted feedback for this event
        if (feedbackRepository.existsByEventAndEmail(feedback.getEvent(), feedback.getEmail())) {
            model.addAttribute("error", "You have already submitted feedback for this event.");
            model.addAttribute("event", feedback.getEvent());
            return "feedback/submit";
        }

        feedbackRepository.save(feedback);
        redirectAttributes.addFlashAttribute("success", "Thank you for your feedback! Your review has been submitted.");
        return "redirect:/events/list";
    }

    // View all feedback (admin view)
    @GetMapping("/all")
    public String viewAllFeedback(Model model) {
        List<Feedback> feedbacks = feedbackRepository.findAllWithEvent();
        model.addAttribute("feedbacks", feedbacks);
        return "feedback/all";
    }

    // View feedback for a specific event
    @GetMapping("/event/{eventId}")
    public String viewEventFeedback(@PathVariable Long eventId, Model model) {
        Optional<Event> event = eventRepository.findById(eventId);
        if (event.isPresent()) {
            List<Feedback> feedbacks = feedbackRepository.findByEventAndApprovedTrueOrderBySubmittedAtDesc(event.get());
            model.addAttribute("event", event.get());
            model.addAttribute("feedbacks", feedbacks);
            return "feedback/event-feedback";
        } else {
            return "redirect:/events/list";
        }
    }

    // Approve feedback
    @GetMapping("/approve/{feedbackId}")
    public String approveFeedback(@PathVariable Long feedbackId, RedirectAttributes redirectAttributes) {
        Optional<Feedback> feedback = feedbackRepository.findById(feedbackId);
        if (feedback.isPresent()) {
            Feedback fb = feedback.get();
            fb.setApproved(true);
            feedbackRepository.save(fb);
            redirectAttributes.addFlashAttribute("success", "Feedback approved successfully.");
        }
        return "redirect:/feedback/all";
    }

    // Delete feedback
    @GetMapping("/delete/{feedbackId}")
    public String deleteFeedback(@PathVariable Long feedbackId, RedirectAttributes redirectAttributes) {
        feedbackRepository.deleteById(feedbackId);
        redirectAttributes.addFlashAttribute("success", "Feedback deleted successfully.");
        return "redirect:/feedback/all";
    }

    // View pending feedback for moderation
    @GetMapping("/pending")
    public String viewPendingFeedback(Model model) {
        List<Feedback> pendingFeedbacks = feedbackRepository.findByApprovedFalseOrderBySubmittedAtDesc();
        model.addAttribute("feedbacks", pendingFeedbacks);
        return "feedback/pending";
    }
}