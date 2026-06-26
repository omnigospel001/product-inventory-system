package com.product.inventory.sysetem.service;

import com.product.inventory.sysetem.dto.UserWithOrdersDto;

import java.util.List;

public interface UserService {
    List<UserWithOrdersDto> getAllUsersWithOrdersFast();
}
