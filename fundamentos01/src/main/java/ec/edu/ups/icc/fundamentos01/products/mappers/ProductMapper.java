package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

public class ProductMapper {

    public static ProductModel toModel(CreateProductDto dto) {

        ProductModel product = new ProductModel();

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCreatedAt(LocalDateTime.now());

        return product;
    }

    public static ProductResponseDto toResponse(ProductModel product) {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStock(product.getStock());

        return response;
    }
}