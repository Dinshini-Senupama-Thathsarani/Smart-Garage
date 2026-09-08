package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.CustomerRequest;
import com.smartgarage.api.entity.Customer;

import java.util.List;

public interface CustomerService {
    Customer create(CustomerRequest request);
    Customer getById(Long id);
    Customer getByUserId(Long userId);
    Customer getByUsername(String username);
    List<Customer> getAll();
    Customer update(Long id, CustomerRequest request);
    void delete(Long id);
}
