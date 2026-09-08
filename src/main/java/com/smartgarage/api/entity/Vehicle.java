package com.smartgarage.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private VehicleModel model;

    @Column(unique = true, nullable = false)
    private String plateNumber;

    @Column(unique = true)
    private String chassisNo;

    private String color;
    private Integer mileage;
}
