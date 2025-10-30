package com.smartevents.event_registration_system.repository;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    

    List<Registration> findByEventOrderByRegistrationDateDesc(Event event);
    

    List<Registration> findByEmailOrderByRegistrationDateDesc(String email);
    

    long countByEventAndStatus(Event event, String status);
    

    boolean existsByEventAndEmail(Event event, String email);
    

    @Query("SELECT r FROM Registration r LEFT JOIN FETCH r.event ORDER BY r.registrationDate DESC")
    List<Registration> findAllWithEvent();
    

    List<Registration> findAllByOrderByRegistrationDateDesc();
}