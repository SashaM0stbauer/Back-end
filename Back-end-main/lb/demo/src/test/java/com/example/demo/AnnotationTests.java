package com.example.demo;

import com.example.demo.annotation.CurrentUser;
import com.example.demo.annotation.CurrentUserArgumentResolver;
import com.example.demo.dto.ProductDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AnnotationTests {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("AC1 & AC2: Valid discount produces no violations")
    void testValidDiscountPrice() {
        ProductDto dto = ProductDto.builder()
                .name("Keyboard")
                .price(new BigDecimal("100.00"))
                .hasDiscount(true)
                .discountPrice(new BigDecimal("80.00"))
                .build();

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty(), "Valid discount must not produce violations");
    }

    @Test
    @DisplayName("AC3: Higher discount price produces clear violation message")
    void testInvalidDiscountPriceHigher() {
        ProductDto dto = ProductDto.builder()
                .name("Mouse")
                .price(new BigDecimal("50.00"))
                .hasDiscount(true)
                .discountPrice(new BigDecimal("60.00"))
                .build();

        Set<ConstraintViolation<ProductDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertEquals("Discount price must be strictly lower than regular price",
                violations.iterator().next().getMessage());
    }

    @Test
    @DisplayName("AC6 & AC7: CurrentUserArgumentResolver extracts user from header")
    void testCurrentUserArgumentResolver() throws Exception {
        CurrentUserArgumentResolver resolver = new CurrentUserArgumentResolver();

        class DummyController {
            @SuppressWarnings("unused")
            public void handle(@CurrentUser String user, String ignored) {}
        }

        MethodParameter validParam = new MethodParameter(
                DummyController.class.getMethod("handle", String.class, String.class), 0);
        MethodParameter invalidParam = new MethodParameter(
                DummyController.class.getMethod("handle", String.class, String.class), 1);

        assertTrue(resolver.supportsParameter(validParam));
        assertFalse(resolver.supportsParameter(invalidParam));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-User-Name", "SashaMostbauer");

        Object resolved = resolver.resolveArgument(validParam, null, new ServletWebRequest(request), null);
        assertEquals("SashaMostbauer", resolved);
    }
}