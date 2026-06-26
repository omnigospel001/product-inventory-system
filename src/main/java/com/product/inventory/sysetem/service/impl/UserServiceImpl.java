package com.product.inventory.sysetem.service.impl;

import com.product.inventory.sysetem.dto.OrderDto;
import com.product.inventory.sysetem.dto.OrderItemDto;
import com.product.inventory.sysetem.dto.UserWithOrdersDto;
import com.product.inventory.sysetem.entity.Order;
import com.product.inventory.sysetem.entity.OrderItem;
import com.product.inventory.sysetem.entity.User;
import com.product.inventory.sysetem.repository.OrderItemRepository;
import com.product.inventory.sysetem.repository.OrderRepository;
import com.product.inventory.sysetem.repository.UserRepository;
import com.product.inventory.sysetem.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserWithOrdersDto> getAllUsersWithOrdersFast() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> userIds = users.stream().map(User::getId).toList();
        List<Order> orders = orderRepository.findByUserIdIn(userIds);
        List<Long> orderIds = orders.stream().map(Order::getId).toList();

        List<OrderItem> orderItems = Collections.emptyList();
        if (!orderIds.isEmpty()) {
            orderItems = orderItemRepository.findByOrderIdInWithProducts(orderIds);
        }

        Map<Long, List<Order>> ordersByUser = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getUser().getId()));
        Map<Long, List<OrderItem>> itemsByOrder = orderItems.stream()
                .collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));

        return users.stream().map(u -> {
            List<OrderDto> userOrders = ordersByUser
                    .getOrDefault(u.getId(), Collections.emptyList())
                    .stream()
                    .map(o -> OrderDto.builder()
                            .id(o.getId())
                            .status(o.getStatus().name())
                            .totalAmount(o.getTotalAmount())
                            .createdAt(o.getCreatedAt())
                            .orderItems(itemsByOrder
                                    .getOrDefault(o.getId(), Collections.emptyList())
                                    .stream()
                                    .map(this::toOrderItemDto)
                                    .collect(Collectors.toList()))
                            .build())
                    .collect(Collectors.toList());

            return UserWithOrdersDto.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .email(u.getEmail())
                    .orders(userOrders)
                    .build();
        }).collect(Collectors.toList());
    }

    private OrderItemDto toOrderItemDto(OrderItem oi) {
        return OrderItemDto.builder()
                .id(oi.getId())
                .productId(oi.getProduct().getId())
                .productName(oi.getProduct().getName())
                .quantity(oi.getQuantity())
                .price(oi.getPrice())
                .build();
    }
}
