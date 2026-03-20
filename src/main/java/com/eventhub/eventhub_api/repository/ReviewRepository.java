package com.eventhub.eventhub_api.repository;

import com.eventhub.eventhub_api.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByEventId(Long eventId);
    List<Review> findByUserId(Long userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.event.id = :eventId")
    Double averageRatingByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.event.id = :eventId")
    Long countByEventId(@Param("eventId") Long eventId);
}
