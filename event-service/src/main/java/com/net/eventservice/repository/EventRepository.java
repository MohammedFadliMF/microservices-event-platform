package com.net.eventservice.repository;

import com.net.eventservice.entities.Event;
import com.net.eventservice.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {
    List<Event> findByOrganizerKeycloakId(String organizerKeycloakId);

    List<Event> findByStatus(EventStatus status);

    @Query("SELECT e FROM Event e WHERE e.eventDate >= :startDate AND e.eventDate <= :endDate")
    List<Event> findEventsBetweenDates(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    @Query("SELECT e FROM Event e WHERE LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Event> findByLocationContaining(@Param("location") String location);

    @Query("SELECT e FROM Event e JOIN e.eventCategories ec WHERE ec.category.id = :categoryId")
    List<Event> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT e FROM Event e WHERE " +
            "(:title IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:location IS NULL OR LOWER(e.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:startDate IS NULL OR e.eventDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.eventDate <= :endDate) AND " +
            "(:status IS NULL OR e.status = :status)")
    List<Event> searchEvents(@Param("title") String title,
                             @Param("location") String location,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("status") EventStatus status);
}
