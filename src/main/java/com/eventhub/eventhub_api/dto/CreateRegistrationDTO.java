package com.eventhub.eventhub_api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateRegistrationDTO {

    @NotEmpty
    @Valid
    private List<RegistrationItemRequestDTO> items;

    public List<RegistrationItemRequestDTO> getItems() { return items; }
    public void setItems(List<RegistrationItemRequestDTO> items) { this.items = items; }
}
