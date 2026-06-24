package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;

/*
 * Interfaz que define las operaciones disponibles
 * para la gestión de productos.
 *
 * Aquí solamente se declaran los métodos.
 * La lógica se implementa en ProductServiceImpl.
 */
public interface ProductService {

    List<ProductResponseDto> findAll();

    Object findOne(Long id);

    ProductResponseDto create(CreateProductDto dto);

    Object update(Long id, UpdateProductDto dto);

    Object partialUpdate(Long id, PartialUpdateProductDto dto);

    Object delete(Long id);
}