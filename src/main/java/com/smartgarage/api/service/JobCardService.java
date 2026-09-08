package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.AssignMechanicRequest;
import com.smartgarage.api.dto.request.UsePartRequest;
import com.smartgarage.api.entity.JobCard;
import com.smartgarage.api.enums.JobCardStatus;

import java.util.List;

public interface JobCardService {
    JobCard createFromBooking(Long bookingId);
    JobCard getById(Long id);
    List<JobCard> getAll();
    List<JobCard> getByStatus(JobCardStatus status);
    JobCard updateStatus(Long id, JobCardStatus status);
    JobCard assignMechanic(Long jobCardId, AssignMechanicRequest request);
    JobCard usePart(Long jobCardId, UsePartRequest request);
    void delete(Long id);
}
