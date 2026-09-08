package com.smartgarage.api.repository;

import com.smartgarage.api.entity.JobCardMechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobCardMechanicRepository extends JpaRepository<JobCardMechanic, Long> {
}
