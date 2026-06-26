package com.product.inventory.sysetem.repository;

import com.product.inventory.sysetem.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("SELECT oi FROM OrderItem oi LEFT JOIN FETCH oi.product p LEFT JOIN FETCH p.category WHERE oi.order.id IN :orderIds")
    List<OrderItem> findByOrderIdInWithProducts(@Param("orderIds") List<Long> orderIds);
}
