package com.smartgarage.api.entity;

import com.smartgarage.api.enums.JobCardStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_cards")
public class JobCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", unique = true)
    private Booking booking;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private JobCardStatus status = JobCardStatus.OPEN;

    private BigDecimal laborCost = BigDecimal.ZERO;
    private BigDecimal totalCost = BigDecimal.ZERO;

    @OneToMany(mappedBy = "jobCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobCardMechanic> jobCardMechanics;

    @OneToMany(mappedBy = "jobCard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobCardPart> jobCardParts;
}
