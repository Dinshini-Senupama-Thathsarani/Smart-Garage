package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.AssignMechanicRequest;
import com.smartgarage.api.dto.request.UsePartRequest;
import com.smartgarage.api.entity.*;
import com.smartgarage.api.enums.BookingStatus;
import com.smartgarage.api.enums.JobCardStatus;
import com.smartgarage.api.exception.DuplicateResourceException;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.*;
import com.smartgarage.api.service.EmailService;
import com.smartgarage.api.service.JobCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobCardServiceImpl implements JobCardService {

    private final JobCardRepository jobCardRepository;
    private final BookingRepository bookingRepository;
    private final MechanicRepository mechanicRepository;
    private final SparePartRepository sparePartRepository;
    private final JobCardMechanicRepository jobCardMechanicRepository;
    private final JobCardPartRepository jobCardPartRepository;
    private final EmailService emailService;

    @Override
    public JobCard createFromBooking(Long bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

            if (jobCardRepository.findByBookingId(bookingId).isPresent()) {
                throw new DuplicateResourceException("A job card already exists for booking id: " + bookingId);
            }

            JobCard jobCard = new JobCard();
            jobCard.setBooking(booking);
            jobCard.setStartDate(LocalDateTime.now());
            jobCard.setStatus(JobCardStatus.OPEN);
            jobCard.setLaborCost(BigDecimal.ZERO);
            jobCard.setTotalCost(BigDecimal.ZERO);
            JobCard saved = jobCardRepository.save(jobCard);

            // Move the booking forward in the workflow
            booking.setStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            log.info("Job card {} created from booking {}", saved.getId(), bookingId);
            return saved;

        } catch (ResourceNotFoundException | DuplicateResourceException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating job card: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public JobCard getById(Long id) {
        return jobCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job card not found with id: " + id));
    }

    @Override
    public List<JobCard> getAll() {
        return jobCardRepository.findAll();
    }

    @Override
    public List<JobCard> getByStatus(JobCardStatus status) {
        return jobCardRepository.findByStatus(status);
    }

    @Override
    public JobCard updateStatus(Long id, JobCardStatus status) {
        JobCard jobCard = getById(id);
        jobCard.setStatus(status);
        if (status == JobCardStatus.COMPLETED) {
            jobCard.setEndDate(LocalDateTime.now());
        }
        log.info("Job card {} status updated to {}", id, status);
        return jobCardRepository.save(jobCard);
    }

    @Override
    public JobCard assignMechanic(Long jobCardId, AssignMechanicRequest request) {
        JobCard jobCard = getById(jobCardId);

        Mechanic mechanic = mechanicRepository.findById(request.getMechanicId())
                .orElseThrow(() -> new ResourceNotFoundException("Mechanic not found with id: " + request.getMechanicId()));

        JobCardMechanic assignment = new JobCardMechanic();
        assignment.setJobCard(jobCard);
        assignment.setMechanic(mechanic);
        assignment.setRoleInJob(request.getRoleInJob());
        jobCardMechanicRepository.save(assignment);

        log.info("Mechanic {} assigned to job card {}", mechanic.getId(), jobCardId);
        return getById(jobCardId);
    }


    @Override
    public JobCard usePart(Long jobCardId, UsePartRequest request) {
        JobCard jobCard = getById(jobCardId);

        SparePart part = sparePartRepository.findById(request.getPartId())
                .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with id: " + request.getPartId()));

        if (part.getStockQty() < request.getQuantity()) {
            throw new IllegalStateException(
                    "Insufficient stock for part '" + part.getPartName() + "'. Available: " + part.getStockQty() + ", requested: " + request.getQuantity());
        }

        // Deduct stock
        part.setStockQty(part.getStockQty() - request.getQuantity());
        sparePartRepository.save(part);

        // Bonus: alert admin if this deduction pushed stock at or below the reorder level
        if (part.getStockQty() <= part.getReorderLevel()) {
            emailService.sendLowStockAlert(part.getPartName(), part.getPartNumber(), part.getStockQty(), part.getReorderLevel());
        }

        // Log usage on the job card at the current unit price
        JobCardPart usage = new JobCardPart();
        usage.setJobCard(jobCard);
        usage.setPart(part);
        usage.setQuantityUsed(request.getQuantity());
        usage.setUnitPriceAtUse(part.getUnitPrice());
        jobCardPartRepository.save(usage);

        // Recalculate job card total cost (labor + parts used so far)
        BigDecimal partsCost = usage.getUnitPriceAtUse().multiply(BigDecimal.valueOf(request.getQuantity()));
        jobCard.setTotalCost(jobCard.getTotalCost().add(partsCost));
        jobCardRepository.save(jobCard);

        log.info("Part {} x{} used on job card {}, stock deducted", part.getPartName(), request.getQuantity(), jobCardId);
        return getById(jobCardId);
    }

    @Override
    public void delete(Long id) {
        JobCard jobCard = getById(id);
        jobCardRepository.delete(jobCard);
    }
}
