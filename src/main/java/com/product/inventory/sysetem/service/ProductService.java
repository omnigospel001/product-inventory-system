package com.product.inventory.sysetem.service;

import com.product.inventory.sysetem.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    Page<ProductDto> getAllProducts(Pageable pageable);
    ProductDto getProductById(Long id);
    ProductDto createProduct(CreateProductRequest request);
    ProductDto updateProduct(Long id, UpdateProductRequest request);
    void deleteProduct(Long id);
    List<ProductDetailDto> getProductsWithReviewsSlow();
    List<ProductDetailDto> getProductsWithReviewsFast();
    List<ProductDetailDto> getProductsWithReviewsCached();
    ProductDto updateProductAndRefreshCache(Long id, UpdateProductRequest request);
    Page<ProductDto> searchProducts(ProductSearchRequest request, Pageable pageable);
}
