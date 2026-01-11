package com.net.eventservice.repository;

import com.net.eventservice.entities.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory,Long> {
}
