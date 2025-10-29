package com.smartevents.event_registration_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "events")
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Event name is required")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "Description is required")
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotNull(message = "Event date is required")
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private int maxAttendees;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    // ✅ Relationship with Registration entity
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Registration> registrations = new ArrayList<>();

    // Constructors
    public Event() {
        // Default constructor
    }

    public Event(String name, String description, LocalDateTime eventDate, String location, int maxAttendees) {
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.location = location;
        this.maxAttendees = maxAttendees;
    }

    // Pre-persist and pre-update methods
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ✅ Registration count method
    public int getRegistrationCount() {
        return (int) registrations.stream()
                .filter(reg -> "REGISTERED".equalsIgnoreCase(reg.getStatus()))
                .count();
    }

    // ✅ Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMaxAttendees() { return maxAttendees; }
    public void setMaxAttendees(int maxAttendees) { this.maxAttendees = maxAttendees; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Registration> getRegistrations() { return registrations; }
    public void setRegistrations(List<Registration> registrations) { this.registrations = registrations; }
}
