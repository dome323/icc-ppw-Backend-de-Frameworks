package ec.edu.ups.icc.fundamentos01.products.entities;

import java.util.HashSet;
import java.util.Set;

import ec.edu.ups.icc.fundamentos01.categories.entities.CategoryEntity;
import ec.edu.ups.icc.fundamentos01.core.entities.BaseEntity;
import ec.edu.ups.icc.fundamentos01.users.entity.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
 * Entidad JPA que representa la tabla products.
 *
 * Cada producto pertenece a un usuario.
 * Cada producto puede pertenecer a varias categorías.
 */
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

    @Column(
            nullable = false,
            length = 150
    )
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    /*
     * Muchos productos pueden pertenecer
     * a un mismo usuario.
     */
    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private UserEntity owner;

    /*
     * Relación muchos a muchos:
     *
     * Un producto puede tener varias categorías.
     * Una categoría puede tener varios productos.
     *
     * La relación se guarda en product_categories.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_categories",
            joinColumns = @JoinColumn(
                    name = "product_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "category_id"
            )
    )
    private Set<CategoryEntity> categories =
            new HashSet<>();

    /*
     * Constructor vacío requerido por JPA.
     */
    public ProductEntity() {
    }

    /*
     * Constructor completo.
     *
     * No recibe ID ni fechas porque esos campos
     * son heredados de BaseEntity.
     */
    public ProductEntity(
            String name,
            Double price,
            Integer stock,
            UserEntity owner,
            Set<CategoryEntity> categories
    ) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.owner = owner;

        this.categories =
                categories != null
                        ? categories
                        : new HashSet<>();
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

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public Set<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(
            Set<CategoryEntity> categories
    ) {
        this.categories =
                categories != null
                        ? categories
                        : new HashSet<>();
    }
}