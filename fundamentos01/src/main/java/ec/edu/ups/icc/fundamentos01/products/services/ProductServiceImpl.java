package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.ConflictException;
import ec.edu.ups.icc.fundamentos01.core.exceptions.domain.NotFoundException;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl
        implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(
            ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    /*
     * Devuelve únicamente productos activos.
     */
    @Override
    public List<ProductResponseDto> findAll() {

        return productRepository.findAll()
                .stream()
                .filter(entity -> !entity.isDeleted())
                .map(Product::fromEntity)
                .map(Product::toResponseDto)
                .toList();
    }

    /*
     * Busca un producto activo.
     */
    @Override
    public ProductResponseDto findOne(Long id) {

        ProductEntity entity =
                findActiveEntity(id);

        return Product.fromEntity(entity)
                .toResponseDto();
    }

    /*
     * Crea un producto.
     *
     * No permite nombres duplicados
     * entre productos activos.
     */
    @Override
    public ProductResponseDto create(
            CreateProductDto dto) {

        Optional<ProductEntity> existingProduct =
                productRepository.findByName(
                        dto.getName());

        if (existingProduct.isPresent()
                && !existingProduct.get().isDeleted()) {

            throw new ConflictException(
                    "Product name already registered");
        }

        Product product = Product.fromDto(dto);

        ProductEntity savedEntity =
                productRepository.save(
                        product.toEntity());

        return Product.fromEntity(savedEntity)
                .toResponseDto();
    }

    /*
     * Actualización completa.
     */
    @Override
    public ProductResponseDto update(
            Long id,
            UpdateProductDto dto) {

        ProductEntity entity =
                findActiveEntity(id);

        Product product =
                Product.fromEntity(entity);

        product.update(dto);

        ProductEntity savedEntity =
                productRepository.save(
                        product.toEntity());

        return Product.fromEntity(savedEntity)
                .toResponseDto();
    }

    /*
     * Actualización parcial.
     */
    @Override
    public ProductResponseDto partialUpdate(
            Long id,
            PartialUpdateProductDto dto) {

        ProductEntity entity =
                findActiveEntity(id);

        Product product =
                Product.fromEntity(entity);

        product.partialUpdate(dto);

        ProductEntity savedEntity =
                productRepository.save(
                        product.toEntity());

        return Product.fromEntity(savedEntity)
                .toResponseDto();
    }

    /*
     * Eliminación lógica.
     *
     * Si no existe o ya está eliminado,
     * responde como recurso no encontrado.
     */
    @Override
    public void delete(Long id) {

        ProductEntity entity =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Product not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException(
                    "Product not found");
        }

        Product product =
                Product.fromEntity(entity);

        product.markAsDeleted();

        productRepository.save(
                product.toEntity());
    }

    /*
     * Busca una entidad y verifica que esté activa.
     */
    private ProductEntity findActiveEntity(
            Long id) {

        ProductEntity entity =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "Product not found"));

        if (entity.isDeleted()) {
            throw new NotFoundException(
                    "Product not found");
        }

        return entity;
    }
}