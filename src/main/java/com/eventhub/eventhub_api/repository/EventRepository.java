package com.eventhub.eventhub_api.repository;

import com.eventhub.eventhub_api.dto.EventDTO;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<EventDTO, Long> {
}
