package ec.edu.ups.icc.fundamentos01.products.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PartialUpdateProductDto {

    @Pattern(
            regexp = ".*\\S.*",
            message = "El nombre no puede estar vacío"
    )
    @Size(
            min = 3,
            max = 150,
            message = "El nombre debe tener entre 3 y 150 caracteres"
    )
    private String name;

    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El precio debe ser mayor o igual a 0"
    )
    private Double price;

    @Min(
            value = 0,
            message = "El stock debe ser mayor o igual a 0"
    )
    private Integer stock;

    public PartialUpdateProductDto() {
    }

    public PartialUpdateProductDto(
            String name,
            Double price,
            Integer stock) {

        this.name = name;
        this.price = price;
        this.stock = stock;
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
}