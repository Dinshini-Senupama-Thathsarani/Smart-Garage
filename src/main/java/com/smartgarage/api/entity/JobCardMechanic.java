package com.smartgarage.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_card_mechanics")
public class JobCardMechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "job_card_id")
    private JobCard jobCard;

    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private Mechanic mechanic;

    private String roleInJob;
}
