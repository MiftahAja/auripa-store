package com.project.auripa.controller;

import com.project.auripa.dto.request.CategoryRequest;
import com.project.auripa.dto.response.CategoryResponse;
import com.project.auripa.dto.response.ResData;
import com.project.auripa.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResData<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(ResData.of("Berhasil", categoryService.getAllCategories()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResData<CategoryResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResData.of("Berhasil", categoryService.getCategoryById(id)));
    }

    @PostMapping
    public ResponseEntity<ResData<CategoryResponse>> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResData.created(categoryService.createCategory(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResData<CategoryResponse>> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ResData.of("Update Berhasil", categoryService.updateCategory(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResData<Void>> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ResData.deleted());
    }
}