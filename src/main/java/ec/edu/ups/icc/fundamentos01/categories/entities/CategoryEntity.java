package ec.edu.ups.icc.fundamentos01.categories.entities;

import java.util.HashSet;
import java.util.Set;

import ec.edu.ups.icc.fundamentos01.core.entities.BaseEntity;
import ec.edu.ups.icc.fundamentos01.products.entities.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

/*
 * Entidad JPA que representa la tabla categories.
 *
 * Una categoría puede estar relacionada
 * con varios productos.
 */
@Entity
@Table(name = "categories")
public class CategoryEntity extends BaseEntity {

    @Column(
            nullable = false,
            unique = true,
            length = 120
    )
    private String name;

    @Column(length = 500)
    private String description;

    /*
     * Lado inverso de la relación ManyToMany.
     *
     * mappedBy debe coincidir con el atributo
     * categories de ProductEntity.
     *
     * ProductEntity administra la tabla intermedia.
     */
    @ManyToMany(
            mappedBy = "categories",
            fetch = FetchType.LAZY
    )
    private Set<ProductEntity> products =
            new HashSet<>();

    /*
     * Constructor vacío requerido por JPA.
     */
    public CategoryEntity() {
    }

    /*
     * Constructor para crear una categoría.
     */
    public CategoryEntity(
            String name,
            String description
    ) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(
            String description
    ) {
        this.description = description;
    }

    public Set<ProductEntity> getProducts() {
        return products;
    }

    public void setProducts(
            Set<ProductEntity> products
    ) {
        this.products =
                products != null
                        ? products
                        : new HashSet<>();
    }
}