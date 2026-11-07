package com.example.demo.annotation;

import com.example.demo.dto.ProductDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PriceDiscountValidator implements ConstraintValidator<ValidPriceDiscount, ProductDto> {

    @Override
    public boolean isValid(ProductDto dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        if (dto.isHasDiscount()) {
            if (dto.getDiscountPrice() == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Discount price is required when discount is enabled")
                        .addPropertyNode("discountPrice")
                        .addConstraintViolation();
                return false;
            }
            if (dto.getPrice() != null && dto.getDiscountPrice().compareTo(dto.getPrice()) >= 0) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Discount price must be strictly lower than regular price")
                        .addPropertyNode("discountPrice")
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}