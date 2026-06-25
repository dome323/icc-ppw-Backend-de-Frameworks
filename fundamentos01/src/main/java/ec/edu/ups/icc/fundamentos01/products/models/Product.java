package ec.edu.ups.icc.fundamentos01.products.models;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;

/*
 * Modelo de dominio del recurso Product.
 *
 * Esta clase sabe cómo construirse desde un DTO,
 * cómo construirse desde una entidad,
 * cómo convertirse nuevamente en entidad
 * y cómo generar el DTO de respuesta.
 */
public class Product {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    /*
     * Constructor vacío.
     */
    public Product() {
    }

    /*
     * Constructor completo.
     */
    public Product(
            Long id,
            String name,
            Double price,
            Integer stock,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean deleted) {

        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }

    /*
     * Factory Method:
     * convierte CreateProductDto en Product.
     */
    public static Product fromDto(CreateProductDto dto) {

        Product product = new Product();

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setDeleted(false);

        return product;
    }

    /*
     * Factory Method:
     * convierte ProductEntity en Product.
     */
    public static Product fromEntity(ProductEntity entity) {

        Product product = new Product();

        product.setId(entity.getId());
        product.setName(entity.getName());
        product.setPrice(entity.getPrice());
        product.setStock(entity.getStock());
        product.setCreatedAt(entity.getCreatedAt());
        product.setUpdatedAt(entity.getUpdatedAt());
        product.setDeleted(entity.isDeleted());

        return product;
    }

    /*
     * Convierte el dominio Product en ProductEntity.
     */
    public ProductEntity toEntity() {

        ProductEntity entity = new ProductEntity();

        entity.setId(this.id);
        entity.setName(this.name);
        entity.setPrice(this.price);
        entity.setStock(this.stock);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        entity.setDeleted(this.deleted);

        return entity;
    }

    /*
     * Convierte Product en ProductResponseDto.
     */
    public ProductResponseDto toResponseDto() {

        ProductResponseDto response = new ProductResponseDto();

        response.setId(this.id);
        response.setName(this.name);
        response.setPrice(this.price);
        response.setStock(this.stock);

        return response;
    }

    /*
     * Actualización completa mediante PUT.
     */
    public void update(UpdateProductDto dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.stock = dto.getStock();
    }

    /*
     * Actualización parcial mediante PATCH.
     */
    public void partialUpdate(PartialUpdateProductDto dto) {

        if (dto.getName() != null) {
            this.name = dto.getName();
        }

        if (dto.getPrice() != null) {
            this.price = dto.getPrice();
        }

        if (dto.getStock() != null) {
            this.stock = dto.getStock();
        }
    }

    /*
     * Marca el producto como eliminado lógicamente.
     */
    public void markAsDeleted() {
        this.deleted = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}