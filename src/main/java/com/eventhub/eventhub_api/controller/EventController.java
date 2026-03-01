package com.eventhub.eventhub_api.controller;

import com.eventhub.eventhub_api.dto.EventDTO;
import com.eventhub.eventhub_api.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private List<Event> events = new ArrayList<>();
    private AtomicLong idCounter = new AtomicLong(1);

    // GET all events
    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(events);
    }

    // GET event by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return events.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404).body(Map.of("error", "Event not found with id: " + id)));
    }

    // POST create event
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO dto) {
        Event event = new Event();
        event.setId(idCounter.getAndIncrement());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setTicketPrice(dto.getTicketPrice());
        event.setCategory(dto.getCategory());
        event.setIsActive(dto.getIsActive());
        event.setEventDate(dto.getEventDate());
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());
        events.add(event);
        return ResponseEntity.status(201).body(dto);
    }

    // PUT update event
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEvent(@PathVariable Long id, @RequestBody EventDTO dto) {
        for (Event event : events) {
            if (event.getId().equals(id)) {
                event.setName(dto.getName());
                event.setDescription(dto.getDescription());
                event.setTicketPrice(dto.getTicketPrice());
                event.setCategory(dto.getCategory());
                event.setIsActive(dto.getIsActive());
                event.setEventDate(dto.getEventDate());
                event.setUpdatedAt(LocalDateTime.now());
                return ResponseEntity.ok(dto);
            }
        }
        return ResponseEntity.status(404).body(Map.of("error", "Event not found with id: " + id));
    }

    // DELETE event
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        boolean removed = events.removeIf(e -> e.getId().equals(id));
        if (removed) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(404).body(Map.of("error", "Event not found with id: " + id));
    }
}