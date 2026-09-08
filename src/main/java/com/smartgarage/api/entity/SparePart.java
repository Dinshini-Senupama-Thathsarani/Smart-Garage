package com.smartgarage.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "spare_parts")
public class SparePart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private SparePartCategory category;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String partName;

    @Column(unique = true)
    private String partNumber;

    private BigDecimal unitPrice;
    private Integer stockQty;
    private Integer reorderLevel;
}
