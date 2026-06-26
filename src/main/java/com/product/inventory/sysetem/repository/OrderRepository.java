package com.product.inventory.sysetem.repository;

import com.product.inventory.sysetem.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.user.id IN :userIds")
    List<Order> findByUserIdIn(@Param("userIds") List<Long> userIds);
}
