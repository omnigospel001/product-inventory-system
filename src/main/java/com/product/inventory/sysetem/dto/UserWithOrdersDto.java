package com.product.inventory.sysetem.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserWithOrdersDto {
    private Long id;
    private String username;
    private String email;
    private List<OrderDto> orders;
}
