package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.LoginRequest;
import com.smartgarage.api.dto.request.RegisterRequest;
import com.smartgarage.api.dto.response.AuthResponse;
import com.smartgarage.api.entity.Customer;
import com.smartgarage.api.entity.User;
import com.smartgarage.api.enums.Role;
import com.smartgarage.api.exception.DuplicateResourceException;
import com.smartgarage.api.repository.CustomerRepository;
import com.smartgarage.api.repository.UserRepository;
import com.smartgarage.api.security.CustomUserDetails;
import com.smartgarage.api.security.JwtUtil;
import com.smartgarage.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new DuplicateResourceException("Username already taken: " + request.getUsername());
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Email already registered: " + request.getEmail());
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            // Self-registration is limited to CUSTOMER; ADMIN/MECHANIC accounts are created by an admin
            user.setRole(request.getRole() == Role.ADMIN ? Role.CUSTOMER : request.getRole());
            User savedUser = userRepository.save(user);

            // Auto-create a Customer profile when the role is CUSTOMER
            if (savedUser.getRole() == Role.CUSTOMER) {
                Customer customer = new Customer();
                customer.setUser(savedUser);
                customer.setFullName(request.getFullName());
                customer.setPhone(request.getPhone());
                customerRepository.save(customer);
            }

            CustomUserDetails userDetails = new CustomUserDetails(savedUser);
            String token = jwtUtil.generateToken(userDetails, savedUser.getRole().name());

            log.info("New user registered: {}", savedUser.getUsername());
            return new AuthResponse(token, savedUser.getUsername(), savedUser.getRole().name(), savedUser.getId());

        } catch (DuplicateResourceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Registration failed: {}", ex.getMessage());
            throw new RuntimeException("Registration failed: " + ex.getMessage());
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            CustomUserDetails userDetails = new CustomUserDetails(user);
            String token = jwtUtil.generateToken(userDetails, user.getRole().name());

            log.info("User logged in: {}", user.getUsername());
            return new AuthResponse(token, user.getUsername(), user.getRole().name(), user.getId());

        } catch (Exception ex) {
            log.error("Login failed for {}: {}", request.getUsername(), ex.getMessage());
            throw new RuntimeException("Invalid username or password");
        }
    }
}
