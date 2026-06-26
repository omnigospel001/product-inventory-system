package com.product.inventory.sysetem.service;

import com.product.inventory.sysetem.dto.CreateInventoryTransactionRequest;
import com.product.inventory.sysetem.dto.InventoryTransactionDto;

import java.util.List;

public interface InventoryTransactionService {
    List<InventoryTransactionDto> getAllTransactions();
    List<InventoryTransactionDto> getTransactionsByProductId(Long productId);
    InventoryTransactionDto createTransaction(CreateInventoryTransactionRequest request);
}
