package com.eventhub.eventhub_api.controller;

import com.eventhub.eventhub_api.dto.CreateEventDTO;
import com.eventhub.eventhub_api.dto.EventDTO;
import com.eventhub.eventhub_api.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/events")
@Tag(name = "Events", description = "Event management endpoints")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    @Operation(summary = "List all events", description = "Returns a paginated list of events with optional filtering and sorting")
    @ApiResponse(responseCode = "200", description = "Events retrieved successfully")
    public ResponseEntity<Page<EventDTO>> getAllEvents(
            @Parameter(description = "Filter by category name") @RequestParam(required = false) String category,
            @Parameter(description = "Minimum ticket price") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "Maximum ticket price") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "Start date filter (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) LocalDateTime startDate,
            @Parameter(description = "End date filter (yyyy-MM-dd'T'HH:mm:ss)") @RequestParam(required = false) LocalDateTime endDate,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field and direction (e.g., title,asc)") @RequestParam(defaultValue = "id,asc") String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<EventDTO> events = eventService.findAllEvents(category, minPrice, maxPrice, startDate, endDate, pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get event by ID", description = "Returns a single event by its ID")
    @ApiResponse(responseCode = "200", description = "Event found", content = @Content(schema = @Schema(implementation = EventDTO.class)))
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findEventById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new event", description = "Creates a new event with the provided details")
    @ApiResponse(responseCode = "201", description = "Event created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventDTO dto) {
        EventDTO created = eventService.createEvent(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an event", description = "Updates an existing event by ID")
    @ApiResponse(responseCode = "200", description = "Event updated successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable Long id, @Valid @RequestBody CreateEventDTO dto) {
        return ResponseEntity.ok(eventService.updateEvent(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an event", description = "Deletes an event by ID")
    @ApiResponse(responseCode = "204", description = "Event deleted successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
