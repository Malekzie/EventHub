package com.eventhub.eventhub_api.service;

import com.eventhub.eventhub_api.dto.CreateRegistrationDTO;
import com.eventhub.eventhub_api.dto.RegistrationDTO;
import com.eventhub.eventhub_api.exception.EventNotFoundException;
import com.eventhub.eventhub_api.exception.RegistrationNotFoundException;
import com.eventhub.eventhub_api.model.*;
import com.eventhub.eventhub_api.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final RegistrationItemRepository registrationItemRepository;
    private final EventRepository eventRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                                RegistrationItemRepository registrationItemRepository,
                                EventRepository eventRepository) {
        this.registrationRepository = registrationRepository;
        this.registrationItemRepository = registrationItemRepository;
        this.eventRepository = eventRepository;
    }

    public Page<RegistrationDTO> findAll(Pageable pageable) {
        return registrationRepository.findAll(pageable).map(this::toDTO);
    }

    public RegistrationDTO findById(Long id) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));
        return toDTO(reg);
    }

    public List<RegistrationDTO> findByUserId(Long userId) {
        return registrationRepository.findByUserId(userId).stream().map(this::toDTO).toList();
    }

    @Transactional
    public RegistrationDTO create(CreateRegistrationDTO dto) {
        Registration registration = new Registration();
        registration.setRegistrationDate(LocalDateTime.now());
        registration.setStatus(Registration.Status.CONFIRMED);

        BigDecimal total = BigDecimal.ZERO;

        for (var itemReq : dto.getItems()) {
            Event event = eventRepository.findById(itemReq.getEventId())
                    .orElseThrow(() -> new EventNotFoundException(itemReq.getEventId()));

            RegistrationItem item = new RegistrationItem();
            item.setRegistration(registration);
            item.setEvent(event);
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(event.getTicketPrice());
            BigDecimal subtotal = event.getTicketPrice()
                    .multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            item.setSubtotal(subtotal);
            total = total.add(subtotal);
            registration.getItems().add(item);
        }

        registration.setTotalAmount(total);
        Registration saved = registrationRepository.save(registration);
        return toDTO(saved);
    }

    @Transactional
    public RegistrationDTO cancel(Long id) {
        Registration reg = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));
        reg.setStatus(Registration.Status.CANCELLED);
        return toDTO(registrationRepository.save(reg));
    }

    private RegistrationDTO toDTO(Registration reg) {
        RegistrationDTO dto = new RegistrationDTO();
        dto.setId(reg.getId());
        dto.setUserId(reg.getUser() != null ? reg.getUser().getId() : null);
        dto.setStatus(reg.getStatus().name());
        dto.setTotalAmount(reg.getTotalAmount());
        dto.setRegistrationDate(reg.getRegistrationDate());
        dto.setItems(reg.getItems().stream()
                .map(item -> new RegistrationDTO.RegistrationItemDTO(
                        item.getId(),
                        item.getEvent().getId(),
                        item.getEvent().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()))
                .toList());
        return dto;
    }
}
