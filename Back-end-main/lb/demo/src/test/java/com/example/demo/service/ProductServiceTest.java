package com.example.demo.service;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import com.example.demo.mapper.ProductMapper;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("getAll: коли є товари в репозиторії, повертає список Dto")
    void getAll_whenProductsExist_returnsDtoList() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Мишка");

        ProductDto dto = new ProductDto();
        dto.setId(1L);
        dto.setName("Мишка");

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDtoList(List.of(product))).thenReturn(List.of(dto));

        List<ProductDto> result = productService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Мишка", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
        verify(productMapper, times(1)).toDtoList(any());
    }

    @Test
    @DisplayName("getById: коли товар знайдено, повертає відповідний Dto")
    void getById_existingId_returnsProductDto() {
        Long productId = 1L;
        Product product = new Product();
        product.setId(productId);
        product.setName("Клавіатура");

        ProductDto expectedDto = new ProductDto();
        expectedDto.setId(productId);
        expectedDto.setName("Клавіатура");

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(expectedDto);

        ProductDto actualDto = productService.getById(productId);

        assertNotNull(actualDto);
        assertEquals(productId, actualDto.getId());
        assertEquals("Клавіатура", actualDto.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("getById: коли товар не існує, кидає RuntimeException 'Product not found'")
    void getById_nonExistingId_throwsRuntimeException() {
        Long nonExistingId = 99L;
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.getById(nonExistingId);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, times(1)).findById(nonExistingId);
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("create: зберігає сутність і перевіряє передані параметри через ArgumentCaptor")
    void create_validDto_savesProductAndReturnsDto() {
        ProductDto inputDto = new ProductDto();
        inputDto.setName("Монітор");
        inputDto.setSku("MON-01");
        inputDto.setPrice(new BigDecimal("7500.00"));
        inputDto.setQuantity(4);

        Product entityToSave = new Product();
        entityToSave.setName("Монітор");
        entityToSave.setSku("MON-01");
        entityToSave.setPrice(new BigDecimal("7500.00"));
        entityToSave.setQuantity(4);

        Product savedEntity = new Product();
        savedEntity.setId(10L);
        savedEntity.setName("Монітор");
        savedEntity.setSku("MON-01");
        savedEntity.setPrice(new BigDecimal("7500.00"));
        savedEntity.setQuantity(4);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setId(10L);
        expectedDto.setName("Монітор");

        when(productMapper.toEntity(inputDto)).thenReturn(entityToSave);
        when(productRepository.save(entityToSave)).thenReturn(savedEntity);
        when(productMapper.toDto(savedEntity)).thenReturn(expectedDto);

        ProductDto actualDto = productService.create(inputDto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());
        Product capturedProduct = captor.getValue();

        assertEquals("Монітор", capturedProduct.getName());
        assertEquals("MON-01", capturedProduct.getSku());
        assertEquals(new BigDecimal("7500.00"), capturedProduct.getPrice());
        assertEquals(10L, actualDto.getId());
    }

    @Test
    @DisplayName("update: коли товар знайдено, оновлює всі поля та зберігає")
    void update_existingId_updatesFieldsAndReturnsDto() {
        Long productId = 5L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Стара назва");
        existingProduct.setSku("OLD-SKU");
        existingProduct.setPrice(new BigDecimal("100.00"));
        existingProduct.setQuantity(1);

        ProductDto updateDto = new ProductDto();
        updateDto.setName("Нова назва");
        updateDto.setSku("NEW-SKU");
        updateDto.setPrice(new BigDecimal("250.00"));
        updateDto.setQuantity(10);

        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Нова назва");
        updatedProduct.setSku("NEW-SKU");
        updatedProduct.setPrice(new BigDecimal("250.00"));
        updatedProduct.setQuantity(10);

        ProductDto expectedDto = new ProductDto();
        expectedDto.setId(productId);
        expectedDto.setName("Нова назва");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(expectedDto);

        ProductDto result = productService.update(productId, updateDto);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());
        Product savedArg = captor.getValue();

        assertEquals("Нова назва", savedArg.getName());
        assertEquals("NEW-SKU", savedArg.getSku());
        assertEquals(new BigDecimal("250.00"), savedArg.getPrice());
        assertEquals(10, savedArg.getQuantity());
        assertEquals(expectedDto.getName(), result.getName());
    }

    @Test
    @DisplayName("update: коли товар для оновлення не знайдено, кидає RuntimeException")
    void update_nonExistingId_throwsRuntimeException() {
        Long nonExistingId = 99L;
        ProductDto updateDto = new ProductDto();
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.update(nonExistingId, updateDto);
        });

        assertEquals("Product not found", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete: викликає deleteById з переданим id")
    void delete_existingId_callsRepositoryDeleteById() {
        Long productId = 3L;

        productService.delete(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }
}