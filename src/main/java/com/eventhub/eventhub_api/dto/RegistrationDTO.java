package com.eventhub.eventhub_api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class RegistrationDTO {

    private Long id;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registrationDate;

    private List<RegistrationItemDTO> items;

    public record RegistrationItemDTO(Long id, Long eventId, String eventName,
                                      Integer quantity, BigDecimal unitPrice, BigDecimal subtotal) {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public List<RegistrationItemDTO> getItems() { return items; }
    public void setItems(List<RegistrationItemDTO> items) { this.items = items; }
}
