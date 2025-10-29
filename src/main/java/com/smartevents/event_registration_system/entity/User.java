package com.smartevents.event_registration_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(nullable = false, length = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Column(unique = true, nullable = false)
    private String email;

    // REMOVE @NotBlank from firstName - we'll generate it from fullName
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "last_name")
    private String lastName;

    @NotBlank(message = "Contact number is required")
    @Column(name = "contact_number", unique = true, nullable = false)
    private String contactNumber;

    private String role = "USER";

    private Double latitude;
    private Double longitude;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public User() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.contactNumber = "000-000-0000";
        this.lastName = "";
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.firstName = "User"; // Default first name
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        
        // Generate firstName from fullName if not set
        if (this.fullName != null && !this.fullName.isEmpty()) {
            String[] nameParts = this.fullName.split(" ", 2);
            this.firstName = nameParts[0];
            if (nameParts.length > 1) {
                this.lastName = nameParts[1];
            }
        }
        
        // Ensure all required fields have values
        if (this.contactNumber == null) {
            this.contactNumber = "000-000-0000";
        }
        if (this.lastName == null) {
            this.lastName = "";
        }
        if (this.firstName == null) {
            this.firstName = "User";
        }
        if (this.latitude == null) {
            this.latitude = 0.0;
        }
        if (this.longitude == null) {
            this.longitude = 0.0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { 
        this.contactNumber = contactNumber; 
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}