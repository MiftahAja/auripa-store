package com.project.auripa.service;

import com.project.auripa.dto.request.ProductRequest;
import com.project.auripa.dto.response.CategoryResponse;
import com.project.auripa.dto.response.ProductResponse;
import com.project.auripa.entity.Category;
import com.project.auripa.entity.Product;
import com.project.auripa.handler.ResourceNotFoundException;
import com.project.auripa.repository.CategoryRepository;
import com.project.auripa.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private ProductResponse mapToResponse(Product product) {
        CategoryResponse catResponse = CategoryResponse.builder()
                .id(product.getCategory().getId())
                .name(product.getCategory().getName())
                .build();

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .isActive(product.getIsActive())
                .category(catResponse)
                .build();
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));
        return mapToResponse(product);
    }

    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak valid"));

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        
        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        return mapToResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak valid"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setUpdatedAt(LocalDateTime.now());

        if (request.getIsActive() != null) {
            product.setIsActive(request.getIsActive());
        }

        return mapToResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produk tidak ditemukan"));

        // Soft Delete
        product.setIsActive(false);
        product.setDeletedAt(LocalDateTime.now());
        productRepository.save(product);
    }
}