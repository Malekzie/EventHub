package com.eventhub.eventhub_api.repository;

import com.eventhub.eventhub_api.model.RegistrationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RegistrationItemRepository extends JpaRepository<RegistrationItem, Long> {

    List<RegistrationItem> findByRegistrationId(Long registrationId);

    List<RegistrationItem> findByEventId(Long eventId);

    @Query("SELECT AVG(ri.subtotal) FROM RegistrationItem ri")
    BigDecimal averageTicketRevenue();

    @Query("SELECT COALESCE(SUM(ri.quantity), 0) FROM RegistrationItem ri WHERE ri.event.id = :eventId")
    Long totalTicketsSoldByEventId(@Param("eventId") Long eventId);
}
