package com.example.demo.mapper;

import com.example.demo.dto.ProductDto;
import com.example.demo.entity.Product;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-07T20:23:00+0200",
    comments = "version: 1.6.3, compiler: Eclipse JDT (IDE) 3.46.200.v20261008-1115, environment: Java 25.0.4.1 (Eclipse Adoptium)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDto toDto(Product entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDto productDto = new ProductDto();

        productDto.setId( entity.getId() );
        productDto.setName( entity.getName() );
        productDto.setPrice( entity.getPrice() );
        productDto.setQuantity( entity.getQuantity() );
        productDto.setSku( entity.getSku() );

        return productDto;
    }

    @Override
    public Product toEntity(ProductDto dto) {
        if ( dto == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.id( dto.getId() );
        product.name( dto.getName() );
        product.price( dto.getPrice() );
        product.quantity( dto.getQuantity() );
        product.sku( dto.getSku() );

        return product.build();
    }

    @Override
    public List<ProductDto> toDtoList(List<Product> entities) {
        if ( entities == null ) {
            return null;
        }

        List<ProductDto> list = new ArrayList<ProductDto>( entities.size() );
        for ( Product product : entities ) {
            list.add( toDto( product ) );
        }

        return list;
    }
}
