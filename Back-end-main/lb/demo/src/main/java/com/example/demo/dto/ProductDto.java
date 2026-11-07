package com.example.demo.dto;

import com.example.demo.annotation.ValidPriceDiscount;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidPriceDiscount
public class ProductDto {
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    private String sku;
    private Integer quantity;

    private boolean hasDiscount;
    private BigDecimal discountPrice;
}