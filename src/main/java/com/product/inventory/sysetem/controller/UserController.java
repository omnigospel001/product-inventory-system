package com.product.inventory.sysetem.controller;

import com.product.inventory.sysetem.dto.UserWithOrdersDto;
import com.product.inventory.sysetem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/with-orders")
    public ResponseEntity<List<UserWithOrdersDto>> getUsersWithOrders() {
        return ResponseEntity.ok(userService.getAllUsersWithOrdersFast());
    }
}
