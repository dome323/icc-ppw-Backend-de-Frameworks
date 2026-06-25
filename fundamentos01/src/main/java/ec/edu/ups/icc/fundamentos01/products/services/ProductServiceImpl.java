package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.List;

import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.Product;
import ec.edu.ups.icc.fundamentos01.products.repositories.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(
            ProductRepository productRepository) {

        this.productRepository = productRepository;
    }

    /*
     * Devuelve solamente productos activos.
     * Los productos eliminados lógicamente se excluyen.
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
     * Busca un producto activo por ID.
     */
    @Override
    public ProductResponseDto findOne(Long id) {

        ProductEntity entity = findActiveEntity(id);

        Product product = Product.fromEntity(entity);

        return product.toResponseDto();
    }

    /*
     * Crea un producto.
     */
    @Override
    public ProductResponseDto create(
            CreateProductDto dto) {

        Product product = Product.fromDto(dto);

        ProductEntity entity = product.toEntity();

        ProductEntity savedEntity =
                productRepository.save(entity);

        Product savedProduct =
                Product.fromEntity(savedEntity);

        return savedProduct.toResponseDto();
    }

    /*
     * Actualización completa.
     * No permite actualizar productos eliminados.
     */
    @Override
    public ProductResponseDto update(
            Long id,
            UpdateProductDto dto) {

        ProductEntity entity = findActiveEntity(id);

        Product product = Product.fromEntity(entity);

        product.update(dto);

        ProductEntity savedEntity =
                productRepository.save(product.toEntity());

        return Product.fromEntity(savedEntity)
                .toResponseDto();
    }

    /*
     * Actualización parcial.
     * No permite actualizar productos eliminados.
     */
    @Override
    public ProductResponseDto partialUpdate(
            Long id,
            PartialUpdateProductDto dto) {

        ProductEntity entity = findActiveEntity(id);

        Product product = Product.fromEntity(entity);

        product.partialUpdate(dto);

        ProductEntity savedEntity =
                productRepository.save(product.toEntity());

        return Product.fromEntity(savedEntity)
                .toResponseDto();
    }

    /*
     * Eliminación lógica.
     * No permite eliminar dos veces.
     */
    @Override
    public void delete(Long id) {

        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException(
                    "Product already deleted");
        }

        Product product = Product.fromEntity(entity);

        product.markAsDeleted();

        productRepository.save(product.toEntity());
    }

    /*
     * Busca el producto y verifica que esté activo.
     */
    private ProductEntity findActiveEntity(Long id) {

        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Product not found"));

        if (entity.isDeleted()) {
            throw new IllegalStateException(
                    "Product is deleted");
        }

        return entity;
    }
}