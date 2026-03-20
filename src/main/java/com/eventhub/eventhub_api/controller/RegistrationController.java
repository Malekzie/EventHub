package com.eventhub.eventhub_api.controller;

import com.eventhub.eventhub_api.dto.CreateRegistrationDTO;
import com.eventhub.eventhub_api.dto.RegistrationDTO;
import com.eventhub.eventhub_api.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/registrations")
@Tag(name = "Registrations", description = "Event registration management")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    @Operation(summary = "List all registrations (paginated)")
    public ResponseEntity<Page<RegistrationDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(registrationService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get registration by ID")
    public ResponseEntity<RegistrationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.findById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get registrations by user ID")
    public ResponseEntity<List<RegistrationDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(registrationService.findByUserId(userId));
    }

    @PostMapping
    @Operation(summary = "Create a new registration")
    public ResponseEntity<RegistrationDTO> create(@Valid @RequestBody CreateRegistrationDTO dto) {
        return ResponseEntity.status(201).body(registrationService.create(dto));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a registration")
    public ResponseEntity<RegistrationDTO> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(registrationService.cancel(id));
    }
}
