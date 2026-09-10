package com.smartgarage.api.service;

import com.smartgarage.api.entity.JobCard;

public interface AiReportService {

    String generateSummary(JobCard jobCard);
}
