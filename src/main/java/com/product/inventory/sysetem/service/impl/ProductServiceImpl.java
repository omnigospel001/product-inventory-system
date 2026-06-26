package com.product.inventory.sysetem.service.impl;

import com.product.inventory.sysetem.dto.*;
import com.product.inventory.sysetem.entity.Category;
import com.product.inventory.sysetem.entity.InventoryTransaction;
import com.product.inventory.sysetem.entity.Product;
import com.product.inventory.sysetem.entity.Review;
import com.product.inventory.sysetem.exception.ResourceNotFoundException;
import com.product.inventory.sysetem.repository.*;
import com.product.inventory.sysetem.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> getAllProducts(Pageable pageable) {
        return productRepository.findAllByDeletedAtIsNull(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDto(product);
    }

    @Override
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        Product saved = productRepository.save(product);

        if (saved.getQuantity() > 0) {
            InventoryTransaction transaction = InventoryTransaction.builder()
                    .product(saved)
                    .quantity(saved.getQuantity())
                    .type("STOCK_IN")
                    .reason("Initial stock on product creation")
                    .build();
            inventoryTransactionRepository.save(transaction);
        }

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            int difference = request.getQuantity() - product.getQuantity();
            if (difference != 0) {
                String type = difference > 0 ? "STOCK_IN" : "STOCK_OUT";
                InventoryTransaction transaction = InventoryTransaction.builder()
                        .product(product)
                        .quantity(Math.abs(difference))
                        .type(type)
                        .reason("Stock adjustment on product update")
                        .build();
                inventoryTransactionRepository.save(transaction);
                product.setQuantity(request.getQuantity());
            }
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }

        Product updated = productRepository.save(product);
        return mapToDto(updated);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDetailDto> getProductsWithReviewsSlow() {
        List<Product> products = productRepository.findAllByDeletedAtIsNull();
        return products.stream()
                .map(this::mapToDetailWithNPlusOne)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDetailDto> getProductsWithReviewsFast() {
        List<Product> products = productRepository.findAllWithCategoryAndReviews();
        return products.stream()
                .map(this::mapToDetailOptimized)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "productDetails", key = "'all'")
    @Transactional(readOnly = true)
    public List<ProductDetailDto> getProductsWithReviewsCached() {
        return getProductsWithReviewsFast();
    }

    @Override
    @CachePut(value = "product", key = "#result.id")
    @CacheEvict(value = "productDetails", allEntries = true)
    @Transactional
    public ProductDto updateProductAndRefreshCache(Long id, UpdateProductRequest request) {
        return updateProduct(id, request);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> searchProducts(ProductSearchRequest request, Pageable pageable) {
        return productRepository.findAll(ProductSpecification.withCriteria(request), pageable)
                .map(this::mapToDto);
    }

    private ProductDetailDto mapToDetailWithNPlusOne(Product p) {
        List<Review> reviews = reviewRepository.findByProductId(p.getId());
        return ProductDetailDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .reviews(reviews.stream().map(this::mapReview).collect(Collectors.toList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ProductDetailDto mapToDetailOptimized(Product p) {
        return ProductDetailDto.builder()
                .id(p.getId())
                .name(p.getName())
                .description(p.getDescription())
                .price(p.getPrice())
                .quantity(p.getQuantity())
                .categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
                .reviews(p.getReviews().stream().map(this::mapReview).collect(Collectors.toList()))
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }

    private ReviewDto mapReview(Review r) {
        return ReviewDto.builder()
                .id(r.getId())
                .userId(r.getUser() != null ? r.getUser().getId() : null)
                .username(r.getUser() != null ? r.getUser().getUsername() : null)
                .rating(r.getRating())
                .comment(r.getComment())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private ProductDto mapToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
