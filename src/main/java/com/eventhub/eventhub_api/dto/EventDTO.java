package com.eventhub.eventhub_api.dto;

import com.eventhub.eventhub_api.model.Event;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventDTO {

    private Long id;
    private String name;
    private String description;
    @NotNull(message = "Ticket price is required")
    private BigDecimal ticketPrice;
    private String category;
    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;

    // No-arg constructor
    public EventDTO() {}

    // All-args constructor
    public EventDTO(Long id, String name, String description, BigDecimal ticketPrice,
                    String category, Boolean isActive, LocalDateTime eventDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ticketPrice = ticketPrice;
        this.category = category;
        this.isActive = isActive;
        this.eventDate = eventDate;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTicketPrice() { return ticketPrice; }
    public void setTicketPrice(BigDecimal ticketPrice) { this.ticketPrice = ticketPrice; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

}