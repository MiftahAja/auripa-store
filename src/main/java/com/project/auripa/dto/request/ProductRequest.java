package com.project.auripa.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductRequest {
    @NotBlank(message = "Nama produk tidak boleh kosong")
    private String name;
    
    private String description;
    
    @NotNull(message = "Harga tidak boleh kosong")
    @Min(value = 0, message = "Harga tidak boleh negatif")
    private BigDecimal price;
    
    @NotNull(message = "Kategori tidak boleh kosong")
    private Long categoryId;
    
    private Boolean isActive;
}