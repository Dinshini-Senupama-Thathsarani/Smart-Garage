package com.smartgarage.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_card_parts")
public class JobCardPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "job_card_id")
    private JobCard jobCard;

    @ManyToOne
    @JoinColumn(name = "part_id")
    private SparePart part;

    private Integer quantityUsed;
    private BigDecimal unitPriceAtUse;
}
