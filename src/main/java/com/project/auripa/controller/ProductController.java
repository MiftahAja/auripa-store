package com.project.auripa.controller;

import com.project.auripa.dto.request.ProductRequest;
import com.project.auripa.dto.response.ProductResponse;
import com.project.auripa.dto.response.ResData;
import com.project.auripa.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ResData<List<ProductResponse>>> getAll() {
        return ResponseEntity.ok(ResData.of("Berhasil", productService.getAllProducts()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResData<ProductResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ResData.of("Berhasil", productService.getProductById(id)));
    }

    @PostMapping
    public ResponseEntity<ResData<ProductResponse>> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResData.created(productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResData<ProductResponse>> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(ResData.of("Update Berhasil", productService.updateProduct(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResData<Void>> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResData.deleted());
    }
}