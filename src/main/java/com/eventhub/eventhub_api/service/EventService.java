package com.eventhub.eventhub_api.service;

import com.eventhub.eventhub_api.dto.EventDTO;
import com.eventhub.eventhub_api.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;

    @Autowired
    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<EventDTO> findAllEvents() {
        return (List<EventDTO>) repository.findAll();
    }

    public EventDTO findEventById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public EventDTO createEvent(EventDTO event) {
        return repository.save(event);
    }

}
