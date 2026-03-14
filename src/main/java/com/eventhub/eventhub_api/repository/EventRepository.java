package com.eventhub.eventhub_api.repository;

import com.eventhub.eventhub_api.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findByCategoryNameIgnoreCase(String categoryName, Pageable pageable);

    @Query("SELECT e FROM Event e WHERE " +
            "(:category IS NULL OR LOWER(e.category.name) = LOWER(:category)) AND " +
            "(:minPrice IS NULL OR e.ticketPrice >= :minPrice) AND " +
            "(:maxPrice IS NULL OR e.ticketPrice <= :maxPrice) AND " +
            "(:startDate IS NULL OR e.eventDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.eventDate <= :endDate)")
    Page<Event> findWithFilters(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
