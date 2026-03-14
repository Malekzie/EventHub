package com.eventhub.eventhub_api.service;

import com.eventhub.eventhub_api.dto.CreateEventDTO;
import com.eventhub.eventhub_api.dto.EventDTO;
import com.eventhub.eventhub_api.exception.CategoryNotFoundException;
import com.eventhub.eventhub_api.exception.EventNotFoundException;
import com.eventhub.eventhub_api.model.Category;
import com.eventhub.eventhub_api.model.Event;
import com.eventhub.eventhub_api.repository.CategoryRepository;
import com.eventhub.eventhub_api.repository.EventRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;

    public EventService(EventRepository eventRepository, CategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
    }

    @Cacheable(value = "events", key = "'all-' + #category + '-' + #minPrice + '-' + #maxPrice + '-' + #startDate + '-' + #endDate + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort")
    public Page<EventDTO> findAllEvents(String category, BigDecimal minPrice, BigDecimal maxPrice,
                                        LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Event> events = eventRepository.findWithFilters(category, minPrice, maxPrice, startDate, endDate, pageable);
        return events.map(this::toDTO);
    }

    @Cacheable(value = "event", key = "#id")
    public EventDTO findEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return toDTO(event);
    }

    @Caching(evict = @CacheEvict(value = "events", allEntries = true))
    public EventDTO createEvent(CreateEventDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));

        Event event = new Event();
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setTicketPrice(dto.getTicketPrice());
        event.setCategory(category);
        event.setIsActive(true);
        event.setEventDate(dto.getEventDate());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        return toDTO(saved);
    }

    @Caching(
            put = @CachePut(value = "event", key = "#id"),
            evict = @CacheEvict(value = "events", allEntries = true)
    )
    public EventDTO updateEvent(Long id, CreateEventDTO dto) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(dto.getCategoryId()));

        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setTicketPrice(dto.getTicketPrice());
        event.setCategory(category);
        event.setEventDate(dto.getEventDate());
        event.setUpdatedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        return toDTO(saved);
    }

    @Caching(evict = {
            @CacheEvict(value = "event", key = "#id"),
            @CacheEvict(value = "events", allEntries = true)
    })
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EventNotFoundException(id);
        }
        eventRepository.deleteById(id);
    }

    private EventDTO toDTO(Event event) {
        return new EventDTO(
                event.getId(),
                event.getName(),
                event.getDescription(),
                event.getTicketPrice(),
                event.getCategory() != null ? event.getCategory().getName() : null,
                event.getEventDate()
        );
    }
}
