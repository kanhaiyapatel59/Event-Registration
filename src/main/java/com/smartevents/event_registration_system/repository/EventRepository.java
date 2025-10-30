package com.smartevents.event_registration_system.repository;

import com.smartevents.event_registration_system.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    

    List<Event> findAllByOrderByEventDateAsc();
    

    List<Event> findByEventDateAfterOrderByEventDateAsc(java.time.LocalDateTime date);
}