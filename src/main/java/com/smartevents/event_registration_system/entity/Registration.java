package com.smartevents.event_registration_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Table(name = "registrations")
public class Registration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
    
    @NotBlank(message = "Full name is required")
    @Column(nullable = false)
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Column(nullable = false)
    private String email;
    
    private String phoneNumber;
    
    private String company;
    
    @Column(updatable = false)
    private LocalDateTime registrationDate;
    
    private String status; // REGISTERED, CANCELLED, ATTENDED
    
    // Constructors
    public Registration() {
        this.registrationDate = LocalDateTime.now();
        this.status = "REGISTERED";
    }

    public Registration(Event event, String fullName, String email, String phoneNumber, String company) {
        this();
        this.event = event;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.company = company;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}