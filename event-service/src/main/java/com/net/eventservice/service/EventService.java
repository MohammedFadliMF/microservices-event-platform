package com.net.eventservice.service;

import com.net.eventservice.dto.*;
import com.net.eventservice.entities.*;
import com.net.eventservice.enums.EventStatus;
import com.net.eventservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventCategoryRepository eventCategoryRepository;

    @Transactional
    public EventDTO createEvent(CreateEventRequest request, String organizerKeycloakId) {
        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setCapacity(request.getCapacity());
        event.setPrice(request.getPrice());
        event.setOrganizerKeycloakId(organizerKeycloakId);
        event.setStatus(EventStatus.ACTIVE);
        event.setAvailableTickets(request.getCapacity());

        event = eventRepository.save(event);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

                EventCategory eventCategory = new EventCategory();
                eventCategory.setEvent(event);
                eventCategory.setCategory(category);
                eventCategoryRepository.save(eventCategory);
            }
        }

        return convertToDTO(event);
    }

    @Transactional
    public EventDTO updateEvent(Long id, UpdateEventRequest request, String organizerKeycloakId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizerKeycloakId().equals(organizerKeycloakId)) {
            throw new RuntimeException("Unauthorized: You can only update your own events");
        }

        if (request.getTitle() != null) event.setTitle(request.getTitle());
        if (request.getDescription() != null) event.setDescription(request.getDescription());
        if (request.getEventDate() != null) event.setEventDate(request.getEventDate());
        if (request.getLocation() != null) event.setLocation(request.getLocation());
        if (request.getCapacity() != null) {
            int difference = request.getCapacity() - event.getCapacity();
            event.setCapacity(request.getCapacity());
            event.setAvailableTickets(event.getAvailableTickets() + difference);
        }
        if (request.getPrice() != null) event.setPrice(request.getPrice());
        if (request.getStatus() != null) event.setStatus(request.getStatus());

        if (request.getCategoryIds() != null) {
            eventCategoryRepository.deleteByEventId(id);
            for (Long categoryId : request.getCategoryIds()) {
                Category category = categoryRepository.findById(categoryId)
                        .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

                EventCategory eventCategory = new EventCategory();
                eventCategory.setEvent(event);
                eventCategory.setCategory(category);
                eventCategoryRepository.save(eventCategory);
            }
        }

        event = eventRepository.save(event);
        return convertToDTO(event);
    }

    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        return convertToDTO(event);
    }

    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> getEventsByOrganizer(String organizerKeycloakId) {
        return eventRepository.findByOrganizerKeycloakId(organizerKeycloakId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<EventDTO> searchEvents(EventSearchRequest searchRequest) {
        if (searchRequest.getCategoryId() != null) {
            return eventRepository.findByCategoryId(searchRequest.getCategoryId()).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        }

        return eventRepository.searchEvents(
                        searchRequest.getTitle(),
                        searchRequest.getLocation(),
                        searchRequest.getStartDate(),
                        searchRequest.getEndDate(),
                        searchRequest.getStatus()
                ).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteEvent(Long id, String organizerKeycloakId) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getOrganizerKeycloakId().equals(organizerKeycloakId)) {
            throw new RuntimeException("Unauthorized: You can only delete your own events");
        }

        eventRepository.delete(event);
    }

    @Transactional
    public boolean updateAvailableTickets(Long eventId, int quantity) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        int newAvailable = event.getAvailableTickets() + quantity;
        if (newAvailable < 0 || newAvailable > event.getCapacity()) {
            return false;
        }

        event.setAvailableTickets(newAvailable);
        eventRepository.save(event);
        return true;
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setTitle(event.getTitle());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setCapacity(event.getCapacity());
        dto.setOrganizerKeycloakId(event.getOrganizerKeycloakId());
        dto.setPrice(event.getPrice());
        dto.setAvailableTickets(event.getAvailableTickets());
        dto.setStatus(event.getStatus());
        dto.setCreatedAt(event.getCreatedAt());
        dto.setUpdatedAt(event.getUpdatedAt());

        List<EventCategory> eventCategories = eventCategoryRepository.findByEventId(event.getId());
        dto.setCategoryIds(eventCategories.stream()
                .map(ec -> ec.getCategory().getId())
                .collect(Collectors.toList()));
        dto.setCategoryNames(eventCategories.stream()
                .map(ec -> ec.getCategory().getName())
                .collect(Collectors.toList()));

        return dto;
    }
}
