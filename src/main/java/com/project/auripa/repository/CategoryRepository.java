package com.project.auripa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.auripa.entity.Category;

@Repository 
public interface  CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByDeletedAtIsNull();
    Optional<Category> findByIdAndDeletedAtIsNull(Long id);
}
