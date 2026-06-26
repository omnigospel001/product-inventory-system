package com.product.inventory.sysetem.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductDetailDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String categoryName;
    private List<ReviewDto> reviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
