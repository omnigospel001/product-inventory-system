package com.product.inventory.sysetem.service.impl;

import com.product.inventory.sysetem.dto.CreateInventoryTransactionRequest;
import com.product.inventory.sysetem.dto.InventoryTransactionDto;
import com.product.inventory.sysetem.entity.InventoryTransaction;
import com.product.inventory.sysetem.entity.Product;
import com.product.inventory.sysetem.exception.ResourceNotFoundException;
import com.product.inventory.sysetem.repository.InventoryTransactionRepository;
import com.product.inventory.sysetem.repository.ProductRepository;
import com.product.inventory.sysetem.service.InventoryTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryTransactionServiceImpl implements InventoryTransactionService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionDto> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryTransactionDto> getTransactionsByProductId(Long productId) {
        return transactionRepository.findByProductId(productId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public InventoryTransactionDto createTransaction(CreateInventoryTransactionRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if ("STOCK_IN".equalsIgnoreCase(request.getType())) {
            product.setQuantity(product.getQuantity() + request.getQuantity());
        } else if ("STOCK_OUT".equalsIgnoreCase(request.getType())) {
            if (product.getQuantity() < request.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - request.getQuantity());
        }

        productRepository.save(product);

        InventoryTransaction transaction = InventoryTransaction.builder()
                .product(product)
                .quantity(request.getQuantity())
                .type(request.getType().toUpperCase())
                .reason(request.getReason())
                .build();

        InventoryTransaction saved = transactionRepository.save(transaction);
        return mapToDto(saved);
    }

    private InventoryTransactionDto mapToDto(InventoryTransaction transaction) {
        return InventoryTransactionDto.builder()
                .id(transaction.getId())
                .productId(transaction.getProduct().getId())
                .productName(transaction.getProduct().getName())
                .quantity(transaction.getQuantity())
                .type(transaction.getType())
                .reason(transaction.getReason())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
