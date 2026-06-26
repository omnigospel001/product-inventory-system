package com.product.inventory.sysetem.controller;

import com.product.inventory.sysetem.dto.CreateInventoryTransactionRequest;
import com.product.inventory.sysetem.dto.InventoryTransactionDto;
import com.product.inventory.sysetem.service.InventoryTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
@RequiredArgsConstructor
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @GetMapping
    public ResponseEntity<List<InventoryTransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(inventoryTransactionService.getAllTransactions());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryTransactionDto>> getTransactionsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryTransactionService.getTransactionsByProductId(productId));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryTransactionDto> createTransaction(@Valid @RequestBody CreateInventoryTransactionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryTransactionService.createTransaction(request));
    }
}
