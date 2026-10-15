package com.project.auripa.service;

import com.project.auripa.dto.request.CategoryRequest;
import com.project.auripa.dto.response.CategoryResponse;
import com.project.auripa.entity.Category;
import com.project.auripa.handler.ResourceNotFoundException;
import com.project.auripa.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse mapToResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByDeletedAtIsNull().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));
        return mapToResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        return mapToResponse(categoryRepository.save(category));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));
        
        category.setName(request.getName());
        category.setUpdatedAt(LocalDateTime.now());
        
        return mapToResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kategori tidak ditemukan"));
        
        // Soft Delete
        category.setDeletedAt(LocalDateTime.now());
        categoryRepository.save(category);
    }
}