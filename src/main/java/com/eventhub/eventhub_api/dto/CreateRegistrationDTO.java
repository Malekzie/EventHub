package com.eventhub.eventhub_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateRegistrationDTO {

    // Nullable — user auth is future scope
    private Long userId;

    @NotEmpty
    @Valid
    private List<RegistrationItemRequestDTO> items;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public List<RegistrationItemRequestDTO> getItems() { return items; }
    public void setItems(List<RegistrationItemRequestDTO> items) { this.items = items; }
}
