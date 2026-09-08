package com.smartgarage.api.repository;

import com.smartgarage.api.entity.JobCardPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCardPartRepository extends JpaRepository<JobCardPart, Long> {
}
