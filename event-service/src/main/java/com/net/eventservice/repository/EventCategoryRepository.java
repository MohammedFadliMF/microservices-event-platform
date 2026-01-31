package com.net.eventservice.repository;

import com.net.eventservice.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    List<EventCategory> findByEventId(Long eventId);
    List<EventCategory> findByCategoryId(Long categoryId);
    void deleteByEventId(Long eventId);
}
