package ec.edu.ups.icc.fundamentos01.products.dtos;

import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(
        description = "Datos requeridos para crear un producto"
)
public class CreateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(
            min = 3,
            max = 150,
            message = "El nombre debe tener entre 3 y 150 caracteres"
    )
    @Schema(
            description = "Nombre del producto",
            example = "Laptop Dell",
            minLength = 3,
            maxLength = 150
    )
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El precio debe ser mayor o igual a 0"
    )
    @Schema(
            description = "Precio del producto",
            example = "1000",
            minimum = "0"
    )
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @DecimalMin(
            value = "0",
            inclusive = true,
            message = "El stock debe ser mayor o igual a 0"
    )
    @Schema(
            description = "Stock del producto",
            example = "10",
            minimum = "0"
    )
    private Integer stock;

    @NotEmpty(message = "Debe seleccionar al menos una categoría")
    @Schema(
            description = "Categorías del producto",
            example = "[1, 2, 3]"
    )
    private Set<Long> categoryIds;

    public CreateProductDto() {
    }

    public CreateProductDto(
            String name,
            Double price,
            Integer stock,
            Set<Long> categoryIds
    ) {
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.categoryIds = categoryIds;
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

    public Set<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(
            Set<Long> categoryIds
    ) {
        this.categoryIds = categoryIds;
    }
}