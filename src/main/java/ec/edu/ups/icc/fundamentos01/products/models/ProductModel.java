
package ec.edu.ups.icc.fundamentos01.products.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.users.entity.UserEntity;

public class ProductModel {
    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private UserEntity owner;
    private List<CategoryEntity> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    public ProductModel() {
    }

 public ProductModel(
            Long id,
            String name,
            Double price,
            Integer stock,
            UserEntity owner,
            List<CategoryEntity> categories,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            boolean deleted
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.owner = owner;

        this.categories =
                categories != null
                        ? categories
                        : new ArrayList<>();

        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(
            Double price
    ) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(
            Integer stock
    ) {
        this.stock = stock;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(
            UserEntity owner
    ) {
        this.owner = owner;
    }

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(
            List<CategoryEntity> categories
    ) {
        this.categories =
                categories != null
                        ? categories
                        : new ArrayList<>();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(
            boolean deleted
    ) {
        this.deleted = deleted;
    }
}