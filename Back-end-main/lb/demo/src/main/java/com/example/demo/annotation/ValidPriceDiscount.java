package com.example.demo.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PriceDiscountValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPriceDiscount {
    String message() default "Discount price must be strictly lower than regular price";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}