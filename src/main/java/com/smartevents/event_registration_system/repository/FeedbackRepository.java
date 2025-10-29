package com.smartevents.event_registration_system.repository;

import com.smartevents.event_registration_system.entity.Event;
import com.smartevents.event_registration_system.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    

    List<Feedback> findByEventOrderBySubmittedAtDesc(Event event);
    

    List<Feedback> findByEventAndApprovedTrueOrderBySubmittedAtDesc(Event event);
    

    List<Feedback> findByEmailOrderBySubmittedAtDesc(String email);
    

    @Query("SELECT f FROM Feedback f JOIN FETCH f.event ORDER BY f.submittedAt DESC")
    List<Feedback> findAllWithEvent();
    

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.event = :event AND f.approved = true")
    Double findAverageRatingByEvent(@Param("event") Event event);
    

    long countByEventAndApprovedTrue(Event event);
    

    boolean existsByEventAndEmail(Event event, String email);
    

    List<Feedback> findByApprovedFalseOrderBySubmittedAtDesc();
}