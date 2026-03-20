package com.eventhub.eventhub_api.repository;

import com.eventhub.eventhub_api.model.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    List<Registration> findByUserId(Long userId);

    List<Registration> findByRegistrationDateBetween(LocalDateTime start, LocalDateTime end);

    Page<Registration> findByStatus(Registration.Status status, Pageable pageable);

    @Query("SELECT COUNT(ri) FROM RegistrationItem ri WHERE ri.event.id = :eventId")
    Long countTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COALESCE(SUM(ri.subtotal), 0) FROM RegistrationItem ri WHERE ri.event.id = :eventId")
    BigDecimal totalRevenueByEventId(@Param("eventId") Long eventId);

    @Query("SELECT DISTINCT r FROM Registration r LEFT JOIN FETCH r.items ri LEFT JOIN FETCH ri.event WHERE r.user.id = :userId")
    List<Registration> findByUserIdWithItems(@Param("userId") Long userId);
}
