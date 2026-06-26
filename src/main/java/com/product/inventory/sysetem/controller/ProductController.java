package com.product.inventory.sysetem.controller;

import com.product.inventory.sysetem.dto.*;
import com.product.inventory.sysetem.service.ProductService;
import com.product.inventory.sysetem.service.RedisService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final RedisService redisService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/slow-with-reviews")
    public ResponseEntity<List<ProductDetailDto>> getProductsSlow() {
        return ResponseEntity.ok(productService.getProductsWithReviewsSlow());
    }

    @GetMapping("/fast-with-reviews")
    public ResponseEntity<List<ProductDetailDto>> getProductsFast() {
        return ResponseEntity.ok(productService.getProductsWithReviewsFast());
    }

    @GetMapping("/cached-with-reviews")
    public ResponseEntity<List<ProductDetailDto>> getProductsCached() {
        return ResponseEntity.ok(productService.getProductsWithReviewsCached());
    }

    @PutMapping("/{id}/update-cache")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ProductDto> updateProductCache(@PathVariable Long id, @Valid @RequestBody UpdateProductRequest request) {
        return ResponseEntity.ok(productService.updateProductAndRefreshCache(id, request));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDto>> searchProducts(ProductSearchRequest request, Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(request, pageable));
    }

    @GetMapping("/redis/{key}")
    public ResponseEntity<Object> getFromRedis(@PathVariable String key) {
        return ResponseEntity.ok(redisService.getValue(key));
    }

    @GetMapping("/redis/product/{id}")
    public ResponseEntity<Object> getProductFromRedisById(@PathVariable Long id) {
        return ResponseEntity.ok(redisService.getValue("product::" + id));
    }
}
