package com.smartevents.event_registration_system.repository;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    // Find all registrations for a specific event
    List<Registration> findByEventOrderByRegistrationDateDesc(Event event);
    
    // Find registrations by email
    List<Registration> findByEmailOrderByRegistrationDateDesc(String email);
    
    // Count registrations for an event
    long countByEventAndStatus(Event event, String status);
    
    // Check if email is already registered for an event
    boolean existsByEventAndEmail(Event event, String email);
    
    // Get all registrations with event data - FIXED VERSION
    @Query("SELECT r FROM Registration r LEFT JOIN FETCH r.event ORDER BY r.registrationDate DESC")
    List<Registration> findAllWithEvent();
    
    // Alternative simpler method if above still causes issues
    List<Registration> findAllByOrderByRegistrationDateDesc();
}