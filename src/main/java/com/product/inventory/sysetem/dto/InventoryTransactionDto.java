package com.product.inventory.sysetem.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InventoryTransactionDto {
    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private String type;
    private String reason;
    private LocalDateTime createdAt;
}
