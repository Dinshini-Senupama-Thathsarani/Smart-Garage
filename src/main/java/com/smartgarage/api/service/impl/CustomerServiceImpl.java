package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.CustomerRequest;
import com.smartgarage.api.entity.Customer;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.CustomerRepository;
import com.smartgarage.api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer create(CustomerRequest request) {
        try {
            Customer customer = new Customer();
            customer.setFullName(request.getFullName());
            customer.setNic(request.getNic());
            customer.setPhone(request.getPhone());
            customer.setAddress(request.getAddress());
            return customerRepository.save(customer);
        } catch (Exception ex) {
            log.error("Error creating customer: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }

    @Override
    public Customer getByUserId(Long userId) {
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No customer profile linked to this account"));
    }

    @Override
    public Customer getByUsername(String username) {
        return customerRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("No customer profile linked to this account"));
    }

    @Override
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @Override
    public Customer update(Long id, CustomerRequest request) {
        Customer customer = getById(id);
        customer.setFullName(request.getFullName());
        customer.setNic(request.getNic());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        return customerRepository.save(customer);
    }

    @Override
    public void delete(Long id) {
        Customer customer = getById(id);
        customerRepository.delete(customer);
    }
}
