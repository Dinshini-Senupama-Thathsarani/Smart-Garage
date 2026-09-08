package com.smartgarage.api.repository;

import com.smartgarage.api.entity.JobCard;
import com.smartgarage.api.enums.JobCardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobCardRepository extends JpaRepository<JobCard, Long> {
    Optional<JobCard> findByBookingId(Long bookingId);
    List<JobCard> findByStatus(JobCardStatus status);
}
