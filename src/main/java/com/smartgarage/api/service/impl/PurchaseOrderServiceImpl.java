package com.smartgarage.api.service.impl;

import com.smartgarage.api.dto.request.PurchaseOrderItemRequest;
import com.smartgarage.api.dto.request.PurchaseOrderRequest;
import com.smartgarage.api.entity.PurchaseOrder;
import com.smartgarage.api.entity.PurchaseOrderItem;
import com.smartgarage.api.entity.SparePart;
import com.smartgarage.api.entity.Supplier;
import com.smartgarage.api.enums.PurchaseOrderStatus;
import com.smartgarage.api.exception.ResourceNotFoundException;
import com.smartgarage.api.repository.PurchaseOrderItemRepository;
import com.smartgarage.api.repository.PurchaseOrderRepository;
import com.smartgarage.api.repository.SparePartRepository;
import com.smartgarage.api.repository.SupplierRepository;
import com.smartgarage.api.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final SupplierRepository supplierRepository;
    private final SparePartRepository sparePartRepository;

    @Override
    public PurchaseOrder create(PurchaseOrderRequest request) {
        try {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + request.getSupplierId()));

            PurchaseOrder order = new PurchaseOrder();
            order.setSupplier(supplier);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(PurchaseOrderStatus.PENDING);

            BigDecimal total = BigDecimal.ZERO;
            PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

            for (PurchaseOrderItemRequest itemRequest : request.getItems()) {
                SparePart part = sparePartRepository.findById(itemRequest.getPartId())
                        .orElseThrow(() -> new ResourceNotFoundException("Spare part not found with id: " + itemRequest.getPartId()));

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(savedOrder);
                item.setPart(part);
                item.setQuantity(itemRequest.getQuantity());
                item.setUnitCost(itemRequest.getUnitCost());
                purchaseOrderItemRepository.save(item);

                total = total.add(itemRequest.getUnitCost().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
            }

            savedOrder.setTotalAmount(total);
            purchaseOrderRepository.save(savedOrder);

            log.info("Purchase order {} created for supplier {}", savedOrder.getId(), supplier.getId());
            return getById(savedOrder.getId());

        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error creating purchase order: {}", ex.getMessage());
            throw ex;
        }
    }

    @Override
    public PurchaseOrder getById(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));
    }

    @Override
    public List<PurchaseOrder> getAll() {
        return purchaseOrderRepository.findAll();
    }

    /**
     * Marks the order RECEIVED and adds each item's quantity back into spare part stock.
     */
    @Override
    public PurchaseOrder receive(Long id) {
        PurchaseOrder order = getById(id);

        if (order.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new IllegalStateException("Purchase order " + id + " has already been received");
        }
        if (order.getStatus() == PurchaseOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot receive a cancelled purchase order");
        }

        for (PurchaseOrderItem item : order.getItems()) {
            SparePart part = item.getPart();
            part.setStockQty(part.getStockQty() + item.getQuantity());
            sparePartRepository.save(part);
        }

        order.setStatus(PurchaseOrderStatus.RECEIVED);
        log.info("Purchase order {} received, stock updated", id);
        return purchaseOrderRepository.save(order);
    }

    @Override
    public PurchaseOrder cancel(Long id) {
        PurchaseOrder order = getById(id);
        if (order.getStatus() == PurchaseOrderStatus.RECEIVED) {
            throw new IllegalStateException("Cannot cancel a purchase order that has already been received");
        }
        order.setStatus(PurchaseOrderStatus.CANCELLED);
        return purchaseOrderRepository.save(order);
    }
}
