package com.product.inventory.sysetem.repository;

import com.product.inventory.sysetem.dto.ProductSearchRequest;
import com.product.inventory.sysetem.entity.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> withCriteria(ProductSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isNull(root.get("deletedAt")));

            if (request.getName() != null && !request.getName().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                        "%" + request.getName().toLowerCase() + "%"));
            }
            if (request.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), request.getCategoryId()));
            }
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
            }
            if (request.getInStock() != null && request.getInStock()) {
                predicates.add(cb.greaterThan(root.get("quantity"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
