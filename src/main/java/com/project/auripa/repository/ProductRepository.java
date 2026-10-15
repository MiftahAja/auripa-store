package com.project.auripa.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.auripa.entity.Product;

public interface  ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByDeletedAtIsNull();
    Optional<Product> findByIdAndDeletedAtIsNull(Long id);
}
